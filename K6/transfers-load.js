import http from 'k6/http';
import { check } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '20s', target: 10 },
        { duration: '10s', target: 0 },
    ],
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