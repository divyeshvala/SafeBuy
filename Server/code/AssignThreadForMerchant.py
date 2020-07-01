import threading
from firebase_admin import db
from MerchantHandler import handleMerchantClients


def assignThreadForMerchantRequests(root):
    path1 = 'NearbyMerchantRequest/{}/MerchantResult'
    path2 = 'NearbyMerchantRequest/{}/ContainmentResult'
    path3 = 'NearbyMerchantRequest/{}'

    while True:
        req = db.reference('NearbyMerchantRequest').get()
        print("Merchant requests : ", req)
        if req is not None:
            for request in req:
                curr_request = req[request]
                if 'assignedThread' not in curr_request or curr_request['assignedThread'] == 'false':
                    tablepath1 = path1.format(request)
                    tablepath2 = path2.format(request)
                    tablepath3 = path3.format(request)
                    root.child(tablepath3).update({'assignedThread': 'true'})
                    worker_thread = threading.Thread(target=handleMerchantClients, args=(curr_request, root, tablepath1, tablepath2, tablepath3,))
                    worker_thread.start()
