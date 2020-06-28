import requests
import json

from Server.code.TransactionVerificationHandler import handlePaymentRequest


def handlePaymentClient(curr_request, root, tablepath1, tablepath2):
    if 'gotResponse' not in curr_request and curr_request['gotResponse'] == 'false':

        handlePaymentRequest(root, tablepath1, curr_request['senderPAN'], curr_request['receiverPAN'])
        root.child(tablepath2).update({'gotResponse': 'true'})

