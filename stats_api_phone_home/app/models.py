from google.appengine.ext import ndb


class Notification(ndb.Model):
  experiment_id = ndb.IntegerProperty(required=True)
  notification_datetime = ndb.DateTimeProperty(auto_add_now=True)

  @staticmethod
  def get_by_experiment_id(experiment_id):
    return Notification.query(Notification.experiment_id == int(experiment_id)).get()
