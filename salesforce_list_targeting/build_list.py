import argparse
import json
import os

import beatbox
import requests
import yaml


CONFIG = yaml.load(open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'config.yaml'), 'r'))


def _fetch_matching_ids(salesforce_object_type, id_attribute, query_condition):
  beatbox_client = beatbox.Client()
  beatbox_client.login(CONFIG['salesforce_username'],
                       CONFIG['salesforce_password'] + CONFIG['salesforce_security_token'])

  soql_query = 'SELECT %s FROM %s WHERE %s' % (id_attribute,
                                               salesforce_object_type,
                                               query_condition.replace('"', "'"))

  return [str(record[2]) for record in beatbox_client.queryAll(soql_query)[beatbox._tPartnerNS.records:]
          if str(record[2])]


def _upload_user_list(project_id, cookie_name, matching_ids, list_name):
  # TODO(jon): Update https://github.com/optimizely/optimizely-client-python with /targeting_lists endpoints and use
  #            that lib in this demo code.

  COOKIE_LIST_TYPE_ID = 1


  targeting_list_data = {
      'description': 'Created via script at'
                     ' https://github.com/optimizely/optimizely-api-samples/salesforce_list_targeting',
      'format': 'csv',
      'key_fields': cookie_name,
      'list_content': ','.join([str(id) for id in matching_ids]),
      'list_type': COOKIE_LIST_TYPE_ID,
      'name': list_name.strip().replace(' ', '_'),
      'project_id': project_id}

  response = requests.post('https://www.optimizelyapis.com/experiment/v1/projects/%s/targeting_lists' % project_id,
                           headers={'Content-Type': 'application/json',
                                    'Token': CONFIG['optimizely_rest_api_token']},
                           data=json.dumps(targeting_list_data))

  response.raise_for_status()


if __name__ == '__main__':
  parser = argparse.ArgumentParser(
      description='Script to build an Optimizely User List for targeting based on Salesforce data')

  parser.add_argument('--salesforce_object_type', required=True,
                      help='The type of Salesforce object you want to build a list from')
  parser.add_argument('--id_attribute', required=True,
                      help='The attribute of the Salesforce object to use as a client-side ID for user list targeting')
  parser.add_argument('--query_condition', required=True,
                      help='The condition to use in the query for matching records, e.g., "LeadSource = \'Website\'"')
  parser.add_argument('--cookie_name', required=True, help='The name of the cookie to use in the user list')
  parser.add_argument('--project_id', required=True, help='The ID of the Optimizely project to upload the list to')
  parser.add_argument('--list_name', required=True, help='A name to use for the uploaded list')

  args = parser.parse_args()

  matching_ids = _fetch_matching_ids(args.salesforce_object_type, args.id_attribute, args.query_condition)

  _upload_user_list(int(args.project_id), args.cookie_name, matching_ids, args.list_name)

  print 'New list created at https://app.optimizely.com/projects/%s/user-lists' % args.project_id
