This sample provides a script that queries for records in a Salesforce account and uploads an
[Optimizely user list](https://help.optimizely.com/hc/en-us/articles/206197347-User-List-Targeting-Create-audiences-based-on-lists-of-data)
to Optimizely based on a cookie value provided on those records, using the
[Optimizely REST API](http://developers.optimizely.com/rest/#targeting-lists).

For instance, if your website stores your customers' account IDs in a cookie called `cust_acct_id`, and you store that
same account ID in the `AccountNumber` field of the Salesforce `Account` object, you could create an Optimizely user
list called "Customers_in_San_Francisco" by invoking the script like so:

```
python build_list.py --salesforce_object_type Account \
                     --id_attribute AccountNumber \
                     --query_condition 'BillingCity = "San Francisco"' \
                     --cookie_name cust_acct_id \
                     --project_id 123456789 \
                     --list_name Customers_in_San_Francisco
```

The result would be a new user list in the 123456789 project, now available for targeting and segmentation via
[Optimizely audiences](https://help.optimizely.com/hc/en-us/articles/200039685-Audiences-Choose-which-visitors-to-include).

To run the script:

- Populate the credentials required in config.yaml.template and re-name that file to config.yaml.
- Ensure you're either using Python 2.7 or, ideally, create a new [virtualenv](https://virtualenv.pypa.io/en/latest/).
- Run the script as described above.

Note that we do not consider this code production-ready, but we hope it's a useful starting point to build on top of
and draw inspiration from!

And as always, feel free to reach out to us here on GitHub, on
[Optiverse](https://community.optimizely.com/t5/Developers/bd-p/Developers), or at <developers@optimizely.com>.
