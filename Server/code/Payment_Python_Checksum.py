import requests
import json

from paytmchecksum import PaytmChecksum

MID = "HPwNbT09210051249462"

paytmParams = {"MID": MID, "ORDERID": "YOUR_ORDER_ID_HERE"}

# Generate checksum by parameters we have
# Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
paytmChecksum = PaytmChecksum.generateSignature(paytmParams, "qSGz5b4U4v18XWle")
verifyChecksum = PaytmChecksum.verifySignature(paytmParams, "qSGz5b4U4v18XWle",paytmChecksum)

print("generateSignature Returns:" + str(paytmChecksum))
print("verifySignature Returns:" + str(verifyChecksum))
