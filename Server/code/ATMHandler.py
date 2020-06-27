from firebase_admin import db

from Server.code.NearbyATMRequestHandler import handleATMRequests
from Server.code.NearbyContainmentRequestHandler import handleContainmentrequests


# from NearbyATMRequestHandler import handleATMRequests
# from NearbyContainmentRequestHandler import handleContainmentrequests

def handlerATMClients(root):
    path1 = 'NearbyATMRequest/{}/ATMResult'
    path2 = 'NearbyATMRequest/{}/ContainmentResult'
    path3 = 'NearbyATMRequest/{}'
    while True:
        req = db.reference('NearbyATMRequest').get()
        print("ATM requests : ", req)
        if req is not None:
            for request in req:
                curr_request = req[request]

                tablepath3 = path3.format(request)
                # print(curr_request['resolved'])

                if "resolvedContainment" in curr_request and curr_request["resolvedContainment"] == 'false':
                    tablepath2 = path2.format(request)

                    # print(tablepath1, tablepath2)

                    print(curr_request)
                    handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
                                              curr_request["distance"])

                    root.child(tablepath3).update({'resolvedContainment': 'true'})

                if "resolvedATM" in curr_request and curr_request["resolvedATM"] == 'false':
                    tablepath1 = path1.format(request)
                    handleATMRequests(root, tablepath1, curr_request["placeName"], curr_request["distance"],
                                      curr_request["distanceUnit"])
                    root.child(tablepath3).update({'resolvedATM': 'true'})
