const fs = require('fs');
const https = require('https');
const http = require('http');

const API_URL = 'http://localhost:8080/api/products/admin';

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function postProduct(product) {
    return new Promise((resolve, reject) => {
        const url = new URL(API_URL);
        const protocol = url.protocol === 'https:' ? https : http;

        const postData = JSON.stringify(product);

        const options = {
            hostname: url.hostname,
            port: url.port || 8080,
            path: url.pathname,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(postData)
            }
        };

        const req = protocol.request(options, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    resolve({
                        success: true,
                        statusCode: res.statusCode,
                        data: data,
                        product: product.productCode
                    });
                } else {
                    resolve({
                        success: false,
                        statusCode: res.statusCode,
                        data: data,
                        product: product.productCode
                    });
                }
            });
        });

        req.on('error', (error) => {
            reject({
                success: false,
                error: error.message,
                product: product.productCode
            });
        });

        req.write(postData);
        req.end();
    });
}

async function uploadProducts(products, delayMs = 100) {


    const results = {
        success: [],
        failed: [],
        total: products.length
    };

    for (let i = 0; i < products.length; i++) {
        const product = products[i];
        const progress = `[${i + 1}/${products.length}]`;

        try {
            console.log(`${progress} posting: ${product.productCode} - ${product.productName}`);

            const result = await postProduct(product);

            if (result.success) {
                console.log(`✓ ${progress} success: ${product.productCode} (: ${result.statusCode})`);
                results.success.push(product.productCode);
            } else {
                console.log(`✗ ${progress} failed: ${product.productCode} (: ${result.statusCode})`);
                results.failed.push({
                    code: product.productCode,
                    statusCode: result.statusCode,
                    response: result.data
                });
            }

        } catch (error) {
            console.log(`✗ ${progress} error: ${product.productCode} - ${error.error || error.message}`);
            results.failed.push({
                code: product.productCode,
                error: error.error || error.message
            });
        }

        if (i < products.length - 1) {
            await delay(delayMs);
        }
    }

    return results;
}

async function main() {
    const args = process.argv.slice(2);

    let inputFile = 'products.json';
    let delayMs = 100;

    for (let i = 0; i < args.length; i++) {
        if (args[i] === '--file' || args[i] === '-f') {
            inputFile = args[i + 1];
            i++;
        } else if (args[i] === '--delay' || args[i] === '-d') {
            delayMs = parseInt(args[i + 1]);
            i++;
        } else if (args[i] === '--help' || args[i] === '-h') {
            console.log('Usage: node upload_products.js [--file <input_file>] [--delay <ms>]');
            console.log('  --file, -f   Input JSON file with products (default: products.json)');
            console.log('  --delay, -d  Delay in milliseconds between requests (default: 100)');
            console.log('  --help, -h   Show this help message');
            return;
        }
    }

    if (!fs.existsSync(inputFile)) {
        console.error(`Error: File "${inputFile}" does not exist!`);
        process.exit(1);
    }

    try {
        const data = fs.readFileSync(inputFile, 'utf8');
        const products = JSON.parse(data);

        if (!Array.isArray(products) || products.length === 0) {
            console.error('Error: JSON file should contain an array of products!');
            process.exit(1);
        }

        console.log(`Read ${products.length} products from ${inputFile}\n`);

        const startTime = Date.now();
        const results = await uploadProducts(products, delayMs);
        const endTime = Date.now();

        console.log('\n' + '='.repeat(60));
        console.log('Upload Complete!');
        console.log('='.repeat(60));
        console.log(`Total: ${results.total}`);
        console.log(`Success: ${results.success.length}`);
        console.log(`Failed: ${results.failed.length}`);
        console.log(`Duration: ${((endTime - startTime) / 1000).toFixed(2)} seconds`);

        if (results.failed.length > 0) {
            console.log('\nFailed Products:');
            results.failed.forEach(item => {
                console.log(`  - ${item.code}: ${item.error || ` ${item.statusCode}`}`);
            });

            const failedFile = 'failed_uploads.json';
            fs.writeFileSync(failedFile, JSON.stringify(results.failed, null, 2), 'utf8');
            console.log(`\nFailed records saved to: ${failedFile}`);
        }

        console.log('='.repeat(60));

    } catch (error) {
        console.error('Error:', error.message);
        process.exit(1);
    }
}

main().catch(error => {
    console.error('Uncaught Error:', error);
    process.exit(1);
});
