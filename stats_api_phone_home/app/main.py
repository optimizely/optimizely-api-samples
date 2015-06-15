import datetime
import os
import urllib


import requests
from twilio import rest
import webapp2
import yaml

import models


CONFIG = yaml.load(open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'config.yaml'), 'r'))


def make_the_call(experiment_name):
  twilio_client = rest.TwilioRestClient(CONFIG['twilio_account_id'], CONFIG['twilio_auth_token'])

  call = twilio_client.calls.create(
      to=CONFIG['notification_recipient_number'],
      from_=CONFIG['notification_sender_number'],
      url='https://%s.appspot.com/call_twiml?%s' % (CONFIG['appengine_app_id'],
                                                    urllib.urlencode({'experiment_name': experiment_name,
                                                                      'auth_token': CONFIG['twilio_auth_token']})))

  return call.sid


class PollHandler(webapp2.RequestHandler):

  CONCLUSIVE_STATUSES = ['winner', 'loser']

  # TODO(jon): Update https://github.com/optimizely/optimizely-client-python with /stats endpoint and use that lib in
  #            this demo code.
  @staticmethod
  def _get_experiments_for_project():
    return requests.get(
        'https://www.optimizelyapis.com/experiment/v1/projects/%s/experiments' % CONFIG['optimizely_project_id'],
        headers={'Token': CONFIG['optimizely_rest_api_token']}).json()

  @staticmethod
  def _get_stats_for_experiment(experiment_id):
    return requests.get('https://www.optimizelyapis.com/experiment/v1/experiments/%s/stats' % experiment_id,
                        headers={'Token': CONFIG['optimizely_rest_api_token']}).json()

  def get(self):
    """Polls stats for experiments for the configured project and sends a significance notification if appropriate.

    Note: This example assumes there is only one goal for each experiment.
    """
    call_sids = []

    for experiment in self._get_experiments_for_project():
      experiment_id = experiment['id']

      existing_notification_record = models.Notification.get_by_experiment_id(experiment_id)

      if existing_notification_record:
        continue

      experiment_stats = self._get_stats_for_experiment(experiment_id)

      for entry in experiment_stats:
        if entry['status'] in self.CONCLUSIVE_STATUSES:
          call_sids.append(make_the_call(experiment['description']))

          notification_record = models.Notification(experiment_id=experiment_id)
          notification_record.put()
          break

    self.response.write('Made calls with Twilio SIDs: %s' % call_sids)


class CallHandler(webapp2.RequestHandler):

  def post(self):
    """Handler providing Twilio with the appropriate TwiML for a given notification."""
    if self.request.get('auth_token') != CONFIG['twilio_auth_token']:
      self.error(401)
      return

    self.response.write("""<?xml version="1.0" encoding="UTF-8"?>
                           <Response>
                             <Say voice="woman">
                               Great news! Your experiment with name %s has reached significance. Have fun at Opp Tee
                               Con 20 15!
                             </Say>
                           </Response>"""
                        % (self.request.get('experiment_name')))


app = webapp2.WSGIApplication([('/poll_stats', PollHandler),
                               ('/call_twiml', CallHandler)],
                              debug=False)
