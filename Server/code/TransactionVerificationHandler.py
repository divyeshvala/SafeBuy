import datetime

import requests
import json

def verifyPayment(senderPAN, receipentPAN, amount, transactionCurrencyCode):
    now = datetime.datetime.now()

    print(now)

    date = now.strftime("%Y-%m-%dT%H:%M:%S")

    print(date)

    url = 'https://sandbox.api.visa.com/visadirect/fundstransfer/v1/pushfundstransactions'

    headers = {'Accept': 'application/json'}

    json_file = open('../Resources/visa_direct_payload.json', )

    payload = json.load(json_file)

    print('Amount : ', amount)

    payload["localTransactionDateTime"] = date
    payload["recipientPrimaryAccountNumber"] = receipentPAN
    payload["senderAccountNumber"] = senderPAN
    payload["amount"] = amount
    payload["transactionCurrencyCode"] = transactionCurrencyCode

    print(payload)

    user_id = '442BYE64PK21LL73EKHO21ao5rYfOa0rYd1zFG4-4h6CAnqVA'

    password = '1144Bv6dOv2rS31kooTm'

    key = '../Resources/visa_direct_api_key.pem'

    cert = '../Resources/visa_direct_api_cert.pem'

    resp = requests.post(url=url, cert=(cert, key), auth=(user_id, password), headers=headers, json=payload)

    print(resp)
    data = json.loads(resp.content)

    res_code_file = open('../Resources/Access_codes.json')

    res_codes = json.load(res_code_file)

    print(data)

    print(res_codes[data['actionCode']])

    return res_codes[data['actionCode']]


def handlePaymentRequest(root, tablepath, senderPAN, receiverPAN, amount, transactionCurrencyCode):
    print("funtion reached")

    print(senderPAN)
    print(receiverPAN)

    response = verifyPayment(senderPAN, receiverPAN, amount, transactionCurrencyCode)

    root.child(tablepath).update({
        'response': response
    })
