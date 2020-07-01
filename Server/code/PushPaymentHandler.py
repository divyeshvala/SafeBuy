from TransactionVerificationHandler import handlePaymentRequest


def handlePaymentClient(curr_request, root, tablepath1, tablepath2):
    if 'gotResponse' not in curr_request or curr_request['gotResponse'] == 'false':

        try:
            handlePaymentRequest(root, tablepath1, curr_request['senderPAN'], curr_request['receiverPAN']
                                 , curr_request['amount'], curr_request['transactionCurrencyCode'])
            root.child(tablepath2).update({'gotResponse': 'success'})
        except:
            root.child(tablepath2).update({'gotResponse': 'failure'})
