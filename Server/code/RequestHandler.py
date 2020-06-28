import threading

from Server.code.ATMHandler import handlerATMClients
from Server.code.MerchantHandler import handleMerchantClients

# from ATMHandler import handlerATMClients
# from MerchantHandler import handleMerchantClients

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Author : Kunal Anand


cred = credentials.Certificate('../Resources/safebuy-23dc8-firebase-adminsdk-tzqju-b6fbe2e44e.json')

app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://safebuy-23dc8.firebaseio.com/'})

root = db.reference()


merchanthandlerthread = threading.Thread(target=handleMerchantClients, args=(root,))

atmhandlerthread = threading.Thread(target=handlerATMClients, args=(root, ))
merchanthandlerthread.start()
atmhandlerthread.start()



