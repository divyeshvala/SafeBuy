from Server.code.NearbyContainmentRequestHandler import *
from Server.code.NearbyATMRequestHandler import *
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# Author : Kunal Anand
# Task to be done : use multithreading to handle requests


cred = credentials.Certificate('./Resources/safebuy-23dc8-firebase-adminsdk'
                               '-tzqju-b6fbe2e44e.json')

app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://safebuy-23dc8.firebaseio.com/'})

root = db.reference()

path1 = 'NearbyATMRequest/{}/ATMResult'
path2 = 'NearbyATMRequest/{}/ContainmentResult'

while True:
    req = db.reference('NearbyATMRequest').get()
    print(req)
    if req is not None:
        for request in req:
            curr_request = req[request]

            # print(curr_request['resolved'])

            if curr_request["resolved"] == 'false':
                tablepath1 = path1.format(request)
                tablepath2 = path2.format(request)

                # print(tablepath1, tablepath2)

                print(curr_request)

                handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
                                          curr_request["distance"])
                curr_request.update({'resolved': 'true'})

                handleATMRequests(root, tablepath1, curr_request["placeName"], curr_request["distance"],
                                  curr_request["distanceUnit"])
    #            t1 = threading.Thread(target=handleATMRequests, args = (root, tablepath1, curr_request["placeName"],
    #                                                                    curr_request["distance"], curr_request["distanceUnit"],))

    #            t2 = threading.Thread(target=handleContainmentrequests, args = (root, tablepath2,curr_request["longitude"],
    #                                           curr_request["latitude"], curr_request["distance"], ))

    #            t1.start()
    #            t2.start()



