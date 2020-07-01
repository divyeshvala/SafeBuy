import threading

from AssignThreadForMerchant import assignThreadForMerchantRequests
from AssignThreadForATM import assignThreadForATMRequests
from AssignThreadForPayment import assignThreadForPaymentRequests

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Author : Kunal Anand


cred = credentials.Certificate('../Resources/safebuy-23dc8-firebase-adminsdk-tzqju-b6fbe2e44e.json')

app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://safebuy-23dc8.firebaseio.com/'})

root = db.reference()


merchanthandlerthread = threading.Thread(target=assignThreadForMerchantRequests, args=(root,))

atmhandlerthread = threading.Thread(target=assignThreadForATMRequests, args=(root, ))

paymenthandlerthread = threading.Thread(target=assignThreadForPaymentRequests, args=(root, ))

merchanthandlerthread.start()
atmhandlerthread.start()
paymenthandlerthread.start()



