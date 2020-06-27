from firebase_admin import db

from Server.code.NearbyContainmentRequestHandler import handleContainmentrequests
from Server.code.NearbyMerchantRequestHandler import handleMerchantRequests

# from NearbyContainmentRequestHandler import handleContainmentrequests
# from NearbyMerchantRequestHandler import handleMerchantRequests


def handleMerchantClients(root):
    path1 = 'NearbyMerchantRequest/{}/MerchantResult'
    path2 = 'NearbyMerchantRequest/{}/ContainmentResult'
    path3 = 'NearbyMerchantRequest/{}'

    while True:
        req = db.reference('NearbyMerchantRequest').get()
        print("Merchant requests : ", req)
        if req is not None:
            for request in req:
                curr_request = req[request]

                # print(curr_request['resolved'])

                tablepath3 = path3.format(request);


                if "resolvedContainment" in curr_request and curr_request["resolvedContainment"] == 'false':
                    tablepath2 = path2.format(request)

                    # print(tablepath1, tablepath2)

                    print(curr_request)

                    handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
                                                     curr_request["distance"])
                    root.child(path3).update({'resolvedContainment': 'true'})

                if "resolvedMerchant" in curr_request and curr_request["resolvedMerchant"] == 'false':

                    tablepath1 = path1.format(request)
                    handleMerchantRequests(root, tablepath1, curr_request["merchantCategoryCode"], curr_request["distance"],
                                             curr_request["distanceUnit"], curr_request["latitude"], curr_request["longitude"])

                    root.child(path3).update({'resolvedMerchant': 'true'})
