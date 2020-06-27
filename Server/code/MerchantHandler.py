from firebase_admin import db

from Server.code.NearbyContainmentRequestHandler import handleContainmentrequests
from Server.code.NearbyMerchantRequestHandler import handleMerchantRequests

# from NearbyContainmentRequestHandler import handleContainmentrequests
# from NearbyMerchantRequestHandler import handleMerchantRequests


def handleMerchantClients(root):
    path1 = 'NearbyMerchantRequest/{}/MerchantResult'
    path2 = 'NearbyMerchantRequest/{}/ContainmentResult'

    while True:
        req = db.reference('NearbyMerchantRequest').get()
        print("Merchant requests : ", req)
        if req is not None:
            for request in req:
                curr_request = req[request]

                # print(curr_request['resolved'])

                if "resolved" in curr_request and curr_request["resolved"] == 'false':
                    tablepath1 = path1.format(request)
                    tablepath2 = path2.format(request)

                    # print(tablepath1, tablepath2)

                    print(curr_request)

                    handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
                                                     curr_request["distance"])
                    curr_request.update({'resolved': 'true'})

                    handleMerchantRequests(root, tablepath1, curr_request["merchantCategoryCode"], curr_request["distance"],
                                             curr_request["distanceUnit"], curr_request["latitude"], curr_request["longitude"])
        #            t1 = threading.Thread(target=handleATMRequests, args = (root, tablepath1, curr_request["placeName"],
        #                                                                    curr_request["distance"], curr_request["distanceUnit"],))

        #            t2 = threading.Thread(target=handleContainmentrequests, args = (root, tablepath2,curr_request["longitude"],
        #                                           curr_request["latitude"], curr_request["distance"], ))

        #            t1.start()
        #            t2.start()

