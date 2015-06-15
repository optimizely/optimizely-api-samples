This sample provides the code for a web application hosted on Google App Engine that uses a handler (`/poll_stats`) to
poll the [Stats Engine](https://www.optimizely.com/statistics) results of all experiments under a particular Optimizely
project (as provided by the [Optimizely REST API](http://developers.optimizely.com/rest/#get-experiment-results-%28stats-engine%29)).

When the application finds that an experiment has reached significance for the first time, it uses the
[Twilio API](https://www.twilio.com/api) to make a phone call to a predefined number, informing the recipient that the
experiment has reached significance.

To run the application locally:

- Ensure you've set up the [Google App Engine Python SDK](https://cloud.google.com/appengine/downloads#Google_App_Engine_SDK_for_Python).
- Populate the credentials and other config information required in config.yaml.template and re-name that file to config.yaml.
- From the `stats_api_phone_home` directory, run: `pip install -t app/libs -r requirements.txt` to install required
libraries.
- If you want to actually deploy the application to Google App Engine, update the application ID in `app.yaml` and config.yaml
accordingly.
- Start up the local application via the Google App Engine SDK.

Note that we do not consider this code production-ready, but we hope it's a useful starting point to build on top of
and draw inspiration from!

And as always, feel free to reach out to us here on GitHub, on
[Optiverse](https://community.optimizely.com/t5/Developers/bd-p/Developers), or at <developers@optimizely.com>.
