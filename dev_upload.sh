#!/bin/bash
curl -F "file=@app/build/outputs/apk/dev/release/com.xiaolian.amigo_dev_1.0.0.sign.zipalign.apk" \
    -F "uKey=50d4e96204e7be0dfcef43829e97703c" \
    -F "_api_key=b7fb050489c7a93733f8ff4bdb1eb636" \
    https://www.pgyer.com/apiv1/app/upload
