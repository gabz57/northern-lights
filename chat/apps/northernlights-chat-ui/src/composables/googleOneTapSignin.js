import {ref} from 'vue';
import * as jose from "jose";

export default function() {
    const CLIENT_ID = process.env.VUE_APP_GOOGLE_CLIENT_ID

    const userData = ref()
    const jwt = ref()

    const googleOptions = {
        client_id: CLIENT_ID, // required
        auto_select: false, // optional
        cancel_on_tap_outside: true, // optional
        context: 'signin', // optional
    };

    const googleOneTap = ({
                              client_id,
                              auto_select = false,
                              cancel_on_tap_outside = false,
                              context = 'signin'
                          }, callback) => {
        const contextValue = ['signin', 'signup', 'use'].includes(context) ? context : 'signin';
        const googleScript = document.createElement('script');
        googleScript.setAttribute('src', 'https://accounts.google.com/gsi/client');
        document.head.appendChild(googleScript)
        // googleScript.onload instead of window.onload because window.onload can be triggered by other libraries
        // and or just missed while googleScript.onload is more consistent
        googleScript.onload = () => {
            if (client_id) {
                window.google.accounts.id.initialize({
                    client_id: client_id,
                    callback: callback,
                    auto_select: auto_select,
                    cancel_on_tap_outside: cancel_on_tap_outside,
                    context: contextValue,

                    // scope: 'profile email',
                    // prompt: 'select_account'

                });
                // Display connect button
                // window.google.accounts.id.renderButton(
                //     document.getElementById("buttonDiv"),
                //     { theme: "outline", size: "large" } // customization attributes
                // );

                // Display the One Tap dialog
                window.google.accounts.id.prompt();
            } else {
                console.error('client_id is missing');
            }
        };
    }

    const oneTapSignin = (options) => {
        googleOneTap(options, (res) => {
            console.log(res);
            jwt.value = res.credential
            const claims = jose.decodeJwt(res.credential);
            console.log(claims);
            // jose.jwtDecrypt(res.credential, {
            //     "n": "1YWUM8Y5UExSfXsBrF6oACI48nITxDf07CiYKn_VTbLRlpXX1AfNtQhrjm-jPjC16qXnGCBhdlZHdCycfezoMg8svo41U7YIVLP5G5H6f7VxAEglmV5IGc0kj35__qmqy3t1Eug_iqxCOyRlcDELQ75MNOhYFQtjeEtLuw4ErpPpOeYVX71vOH3Q9epItMM0n18FXW5Dd6BkCiHvMkb5eSHOH07J0h-MkRF133R-YSPPgDlqLeRxdjDo2rwqKFsOa68edzconVcETWR2YSoFtangVd-IBhzFrax8gyVsntKpmbg8XyJZU2vtgMiTdP0wAjAe8gy78Dg1WIOVOe58lQ",
            //     "use": "sig",
            //     "alg": "RS256",
            //     "kty": "RSA",
            //     "kid": "cec13debf4b96479683736205082466c14797bd0",
            //     "e": "AQAB"
            // }, {
            //     issuer: 'urn:example:issuer',
            //     audience: 'urn:example:audience'
            // }).then(({ payload, protectedHeader })=> {
            //     userData.value = payload.data

                userData.value = claims

            // })


            // userData.value = res.data

            // // Send response to server
            // // console.log(res);
            //
            // // Google One-Tap Signin sends a POST request which must be sent to a server to be processed.
            // fetch("https://gabzio.freeboxos.fr:9090" + "/verify-token", {
            //     method: 'POST',
            //     // body: JSON.stringify(res), // string or object
            //     body: res, // string or object
            //     headers: new Headers({
            //         // "Accept": "application/json",
            //         // "Content-Type": "application/json",
            //         // Access-Control-Allow-Origin is mandatory as this is a "Complex" request.
            //         // This must match the "origin" in the CorsOptions in the backend, or it will fail the preflight.
            //         "Access-Control-Allow-Origin": CLIENT_URL
            //     })
            // })
            //     .then(res => {
            //         // Continue Auth Flow with data from response.data
            //         console.log(res);
            //         userData.value = res.data
            //     })
            //     .catch(error => {
            //         console.log(error);
            //     });

        });
    }

    return {googleOptions, oneTapSignin, userData, jwt}
}
