package com.org.uptime_monitor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(value = "/health", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getHealth() {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>System Health</title>
                <style>
                    body {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        background-color: #f8fafc;
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    }
                    .health-wrapper {
                        display: flex;
                        align-items: baseline;
                        background: white;
                        padding: 2rem 4rem;
                        border-radius: 12px;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.05);
                    }
                    h1 {
                        color: #22c55e; /* Vibrant Green */
                        font-size: 6rem;
                        margin: 0;
                        line-height: 1;
                    }
                    h2 {
                        color: #334155;
                        font-size: 3rem;
                        margin: 0;
                        font-weight: 400; /* Normal weight */
                    }
                    /* Mobile responsiveness */
                    @media (max-width: 600px) {
                        .health-wrapper { padding: 1.5rem 2rem; }
                        h1 { font-size: 4.5rem; }
                        h2 { font-size: 2.2rem; }
                    }
                </style>
            </head>
            <body>
                <div class="health-wrapper">
                    <h1>H</h1>
                    <h2>ealth is OK</h2>
                </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok(html);
    }

    @GetMapping("/cpu-test")
    public ResponseEntity<Long> cpuTest(
            @RequestParam(defaultValue = "100000000") long iterations) {

        long result = 0;

        for (long i = 0; i < iterations; i++) {
            result += i * i;
        }

        return ResponseEntity.ok(result);
    }
}
