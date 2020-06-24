# Code for Testing Global ATM Locator API

import requests
import json
import os

url = 'https://sandbox.api.visa.com/merchantlocator/v1/locator'


headers = {'Accept': 'application/json'}

json_file = open('../Resources/merchant_locator_payload.json',)

payload = json.load(json_file)


user_id = 'XC2ZRLC7JCX0B2PR5SWK21t2Or5cIWdZsdfe9DAw4vfq30040'

password = 'BdjGC5WegJZ13qit60o2'

key = '../Resources/merchant_locator_api_key.pem'

cert = '../Resources/merchant_locator_api_cert.pem'

resp = requests.post(url=url, cert=(cert, key), auth=(user_id, password), headers=headers, json=payload, timeout=10)

data = json.loads(resp.content)
print(data)
