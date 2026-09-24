import http from 'k6/http';
import { check } from 'k6';

// export const options = {
//     summaryTrendStats: [
//         'avg',
//         'min',
//         'med',
//         'max',
//         'p(90)',
//         'p(95)',
//         'p(99)',
//     ],
//
//     thresholds: {
//         http_req_failed: ['rate<0.01'],
//         http_req_duration: ['p(95)<100'],
//     },
//
//     stages: [
//         { duration: '15s', target: 10 },
//         { duration: '15s', target: 25 },
//         { duration: '15s', target: 50 },
//         { duration: '15s', target: 100 },
//         { duration: '15s', target: 0 },
//     ],
// };

export const options = {
    vus: 45,
    duration: '30s',

    summaryTrendStats: [
        'avg',
        'min',
        'med',
        'max',
        'p(90)',
        'p(95)',
        'p(99)',
    ],

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<100'],
    },
};

export default function () {
    const url = 'http://localhost:8080/transfers';

    const payload = JSON.stringify({
        senderId: '820ce3fa-7131-4566-8413-2ad8d698b769',
        receiverId: '550e8400-e29b-41d4-a716-446655440001',
        amount: 300,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Idempotency-Key': crypto.randomUUID(),
        },
    };

    const response = http.post(url, payload, params);

    check(response, {
        'status is 201': (r) => r.status === 201,
    });
}