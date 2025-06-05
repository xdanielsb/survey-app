import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: __ENV.VUS ? parseInt(__ENV.VUS) : 1000, // 1000 virtual users
    duration: __ENV.DURATION || '1m',
};

const BASE_URL = __ENV.BASE_URL;
const ENDPOINT = __ENV.ENDPOINT || '/surveys';

export default function () {
    let res = http.get(`${BASE_URL}${ENDPOINT}`);
    let ok = check(res, {
        'GET endpoint status is 200': (r) => r.status === 200,
        'Response is JSON': (r) => {
            try { JSON.parse(r.body); return true; } catch (_) { return false; }
        }
    });
    if (!ok) {
        console.error(`Status: ${res.status} | Body: ${res.body}`);
    }
    sleep(Math.random() * 2);
}
