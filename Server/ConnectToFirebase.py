import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate('safebuy-23dc8-firebase-adminsdk-tzqju-b6fbe2e44e.json')

app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://safebuy-23dc8.firebaseio.com/'})

root = db.reference()
# Add a new user under /users.

#new_user = root.child('users').push({
 #   'name': 'Mary Anning',
 #   'since': 1700
#})

#Retrieve new
mary = db.reference('users').get()

for data in mary:
    if data == 'Test':
        print(mary[data])


