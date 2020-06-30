from firebase_admin import db

from Server.code.NearbyContainmentRequestHandler import handleContainmentrequests
from Server.code.NearbyMerchantRequestHandler import handleMerchantRequests


def handleMerchantClients(curr_request, root, tablepath1, tablepath2, tablepath3):
    if "resolvedContainment" in curr_request and curr_request["resolvedContainment"] == 'false':

        print(curr_request)

        dis = curr_request["distance"]

        if curr_request["distanceUnit"] == 'KM':
            dis = dis + "000"
        try:
            handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
                                      dis)
            root.child(tablepath3).update({'resolvedContainment': 'success'})
        except:
            root.child(tablepath3).update({'resolvedContainment': 'failure'})

    if "resolvedMerchant" in curr_request and curr_request["resolvedMerchant"] == 'false':
        print(curr_request["resolvedMerchant"])

        try:
            handleMerchantRequests(root, tablepath1, curr_request["merchantCategoryCode"], curr_request["distance"],
                                   curr_request["distanceUnit"], curr_request["latitude"], curr_request["longitude"])
            root.child(tablepath3).update({'resolvedMerchant': 'success'})
        except:
            root.child(tablepath3).update({'resolvedMerchant': 'failure'})
