# codes to use firebase api
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json

json_file = open('./Resources/safebuy-23dc8-firebase-adminsdk-tzqju-b6fbe2e44e.json',)

payload = json.load(json_file)

cred = credentials.Certificate(payload)

app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://safebuy-23dc8.firebaseio.com/'})

root = db.reference()
# Add a new user under /users.

new_user = root.child('NearbyATMRequest').push({
    'placeName': 'Vile Parle',
    'distance': '20',
    'distanceUnit': 'km'
})

new_user.update({'distance': 30})

# Retrieve new

# while True:
# requests = db.reference('NearbyATMRequest').get()

# for request in requests:
#    curr_request = requests[request]
#    lat = curr_request['latitude']
#    lon = curr_request['longitude']

#    print(lat)
#    print(lon)

#   root.child('NearbyATMRequest/'+request+'/result').push({
#       'result': 'distinction'
#   })
