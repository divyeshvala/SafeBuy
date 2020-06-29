import threading
from firebase_admin import db

from Server.code.PushPaymentHandler import handlePaymentClient


def assignThreadForPaymentRequests(root):
    path1 = 'PaymentRequest/{}/Result'
    path2 = 'PaymentRequest/{}'

    while True:
        req = db.reference('PaymentRequest').get()
        print("Payment requests : ", req)

        if req is not None:
            for request in req:
                curr_request = req[request]

                if 'assignedThread' not in curr_request or curr_request['assignedThread'] == 'false':
                    tablepath1 = path1.format(request)
                    tablepath2 = path2.format(request)
                    root.child(tablepath2).update({'assignedThread': 'true'})
                    worker_thread = threading.Thread(target=handlePaymentClient, args=(curr_request, root, tablepath1, tablepath2))
                    worker_thread.start()