import threading
from firebase_admin import db
from ATMHandler import handleATMClients


def assignThreadForATMRequests(root):
    path1 = 'NearbyATMRequest/{}/ATMResult'
    path2 = 'NearbyATMRequest/{}/ContainmentResult'
    path3 = 'NearbyATMRequest/{}'

    while True:
        req = db.reference('NearbyATMRequest').get()
        print("ATM requests : ", req)
        if req is not None:
            for request in req:
                curr_request = req[request]
                if 'assignedThread' not in curr_request or curr_request['assignedThread'] == 'false':
                    tablepath1 = path1.format(request)
                    tablepath2 = path2.format(request)
                    tablepath3 = path3.format(request)
                    root.child(tablepath3).update({'assignedThread': 'true'})
                    worker_thread = threading.Thread(target=handleATMClients, args=(curr_request, root, tablepath1, tablepath2, tablepath3,))
                    worker_thread.start()
