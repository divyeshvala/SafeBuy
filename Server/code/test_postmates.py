import postmates as pm


api = pm.PostmatesAPI("cus_MjOJqWd3d3Iee-", "6a095ff3-8d84-4d8c-9d17-14531c69300c")

pickup = pm.Location('Alice', '100 Start St, San Francisco, CA', '415-555-0000')
dropoff = pm.Location('Bob', '200 End St, San Francisco, CA', '415-777-9999')

# quote = pm.DeliveryQuote(api, pickup.address, dropoff.address)

delivery = pm.Delivery(api, 'a manifest', pickup, dropoff)
delivery.create()