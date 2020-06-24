import requests
import json


# Author : Kunal Anand
# The code is successfully returning  ATMs in a given radius to a given location

def getATMLocations(placeName, distance, distanceUnit):
    url = 'https://sandbox.api.visa.com/globalatmlocator/v1/localatms/atmsinquiry'

    headers = {'Accept': 'application/json'}

    json_file = open('../Resources/atm_locator_payload.json',)

    payload = json.load(json_file)

    # print(payload["requestData"]["location"]["placeName"])

    payload["requestData"]["location"]["placeName"] = placeName
    payload["requestData"]["distance"] = distance
    payload["requestData"]["distanceUnit"] = distanceUnit

    # print(payload["requestData"]["location"]["placeName"])

    user_id = 'XB51RTNUVXED4Q8QCL1F21xtGnujtomXrACc_4tXL3WH1ZsdQ'

    password = 'AA7AcWiDQ3PLwCmUhflz4rq0MiYw'

    key = '../Resources/atm_locator_api_key.pem'

    cert = '../Resources/atm_locator_api_cert.pem'

    resp = requests.post(url=url, cert=(cert, key), auth=(user_id, password), headers=headers, json=payload, timeout=10)

    data = json.loads(resp.content)

    return data


def handleATMRequests(root, tablepath, city, distance, distanceUnit):

    response = getATMLocations(city, distance, distanceUnit)

    if response["responseData"] is not None:
        for res in response["responseData"]:

            print('Found locations',res["foundATMLocations"])

            if res["foundATMLocations"] is not None:
                for locations in res["foundATMLocations"]:

                    print(locations["location"]["placeName"], "latitude: ", locations["location"]["coordinates"]["latitude"],
                            "longitude: ", locations["location"]["coordinates"]["longitude"])

                    root.child(tablepath).push({
                        "placeName": locations["location"]["placeName"],
                        "coordinates": {
                            "latitude": locations["location"]["coordinates"]["latitude"],
                            "longitude": locations["location"]["coordinates"]["longitude"],
                        }
                    })
            root.child(tablepath).push({
                "placeName": None,
                "coordinates": {
                    "latitude": -360,
                    "longitude": -360,
                }
            })

