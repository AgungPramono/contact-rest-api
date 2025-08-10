import http from 'k6/http';
import {check} from 'k6';

// Konfigurasi skenario pengujian
export let options = {
    stages: [
        { duration: '10s', target: 50 },   // Ramp up ke 50 virtual users dalam 10 detik
        { duration: '50s', target: 100 },  // Ramp up ke 100 virtual users dalam 50 detik
        { duration: '10s', target: 0 },    // Ramp down ke 0 virtual users dalam 10 detik
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% request harus selesai dalam 500ms
        http_req_failed: ['rate<0.1'],    // Error rate harus di bawah 10%
    },
};

// Fungsi utama yang akan dijalankan oleh setiap virtual user
export default function () {
    // URL endpoint yang akan diuji
    const url = 'http://localhost:8080/api/hello';

    // Header yang diperlukan
    const headers = {
        'X-API-TOKEN': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFndW5nIiwibmFtZSI6ImFndW5nIiwiZXhwIjoxNzU3NDMyNTE0fQ.Ki3QtBG8ZWHeRHh4-4_VJHDUhRHUCkVjhKGDp2MiAtc',
        'Content-Type': 'application/json',
    };

    // Melakukan GET request
    let response = http.get(url, { headers: headers });

    // Validasi response
    check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
        'has valid response': (r) => r.body && r.body.length > 0,
    });

}

// Fungsi setup (opsional) - dijalankan sekali sebelum test dimulai
export function setup() {
    console.log('Starting load test...');
    console.log('Target URL: http://localhost:8080/api/hello');
    console.log('Test duration: 70 seconds total');
}

// Fungsi teardown (opsional) - dijalankan sekali setelah test selesai
export function teardown(data) {
    console.log('Load test completed!');
}