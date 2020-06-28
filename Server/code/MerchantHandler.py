from firebase_admin import db

from Server.code.NearbyContainmentRequestHandler import handleContainmentrequests
from Server.code.NearbyMerchantRequestHandler import handleMerchantRequests


def handleMerchantClients(curr_request, root, tablepath1, tablepath2, tablepath3):

    if "resolvedContainment" in curr_request and curr_request["resolvedContainment"] == 'false':

        # print(curr_request)
        #
        # handleContainmentrequests(root, tablepath2, curr_request["longitude"], curr_request["latitude"],
        #                                  curr_request["distance"])
        root.child(tablepath3).update({'resolvedContainment': 'true'})

    if "resolvedMerchant" in curr_request and curr_request["resolvedMerchant"] == 'false':

        handleMerchantRequests(root, tablepath1, curr_request["merchantCategoryCode"], curr_request["distance"],
                                 curr_request["distanceUnit"], curr_request["latitude"], curr_request["longitude"])

        root.child(tablepath3).update({'resolvedMerchant': 'true'})
