# BetterSubaruRemoteStart
A reverse engineered implementation of the MySubaru App that allows for remote start from a WearOS watch. The WearOS app communicates with a companion app on your Android phone to send the request.

# To obtain DeviceID and Vehicle Key to setup login: 
1. Go to https://www.mysubaru.com/login.html on a desktop browser with access to debug tools, and log in with your username and password.
2. When prompted for your 2-step verification, make sure you click "Remember this device"
3. Once logged in, right click and press inspect element.
4. Go to "Application" in the top bar.
   a. ![image](https://github.com/krishnapatel02/BetterSubaruRemoteStart/assets/30353953/faa9f4c6-6200-442b-a526-c05e5919a7a2)
5. Go to Local Storage, and click mysubaru.com
   a. ![image](https://github.com/krishnapatel02/BetterSubaruRemoteStart/assets/30353953/8a634271-5ad5-4dcc-838a-72d5704772df)

6. Find the CWP_DEVICE_ID_{YOUREMAILHERE} key, this is your device ID, it should be 13 numbers long
7. Find the lastSelectedVehicleKey{YOUREMAILHERE} key, this is your vehicle key, it should be 7 numbers long
8. Type this information into the app along with your username, password, and PIN on the screen below,
   a. ![image](https://github.com/krishnapatel02/BetterSubaruRemoteStart/assets/30353953/a435fd8b-9f25-4930-8b0f-450a051dd12e)
9. If you would like to label this device on your account, click "My Profile" and then my devices
   a.   ![image](https://github.com/krishnapatel02/BetterSubaruRemoteStart/assets/30353953/5ea8aaf1-3028-4a56-af05-f810035dbef7)
10. Edit the device names by clicking the pencil, or delete them.
   a. ![image](https://github.com/krishnapatel02/BetterSubaruRemoteStart/assets/30353953/388db374-e171-4bb9-9c66-24786677564f)

DISCLAIMERS:
1. This app has only been tested on my car, models older than 2023 might have limitations.
   a. This will not work on Solterras as that car is based off a Toyota and has a different implementation.
2. I am not responsible for any issues this may cause with your vehicle/ Subaru account, you assume all risks.
3. For the watch start feature to work, your phone must be in range and accessible via Bluetooth. Additionally, both devices must have their relative apps installed.
4. Please enable text notifications and push notifications on the original app. My implementation simply sends the commands and does not look for a response on if a Lock/Unlock/Engine Start was successful.
