const fs = require('fs');

const categoryIds = [
    "1420d2ef-d5fd-5c1b-b54c-08dfe1a09b68",
    "34bcca30-1c8f-5a9c-a0f5-03805c4d9f90",
    "557ce3a7-2fa8-5137-bdf9-258194f1fac2",
    "80b36018-898d-5d01-93c2-864b1bea13fb",
    "872e8fc3-4913-5723-be65-9ba332a8bc0b",
    "9c3b9b6a-2319-52ba-ac5a-68f9345d64fa",
    "a81a3f7f-9140-599c-813e-301024518ba0",
    "b55b90c3-8ba7-550f-a1f4-818b7639c403",
    "b8c50e3f-5e59-5177-adac-43061cf359be",
    "ba19a845-7874-5aae-8a21-55a3dd7faf3a",
    "d56ea216-65b1-5726-a5e4-d59b98ad0959",
    "e9f387dd-5a32-5647-a4c6-8d9d3ae2982d"
];

const productTypes = [
    { name: "T-Shirt", code: "TSH" },
    { name: "Polo Shirt", code: "POL" },
    { name: "Dress Shirt", code: "DRS" },
    { name: "Sweater", code: "SWT" },
    { name: "Jacket", code: "JKT" },
    { name: "Jeans", code: "JNS" },
    { name: "Trousers", code: "TRS" },
    { name: "Shorts", code: "SHT" },
    { name: "Dress", code: "DRS" },
    { name: "Skirt", code: "SKT" },
    { name: "Hoodie", code: "HOD" },
    { name: "Blazer", code: "BLZ" }
];

const seasons = ["Spring", "Summer", "Autumn", "Winter", "All Season"];

const collections = [
    "Spring 2025", "Summer 2025", "Autumn 2025", "Winter 2025",
    "Classic Collection", "Modern Collection", "Premium Collection",
    "Casual Collection", "Formal Collection", "Sport Collection"
];

const materials = [
    "Cotton", "Polyester", "Wool", "Silk", "Linen",
    "Cotton Blend", "Denim", "Leather", "Cashmere", "Viscose"
];

const colorSets = [
    ["#000000", "#FFFFFF"],
    ["#0A2540", "#6B705C"],
    ["#FF6B6B", "#4ECDC4"],
    ["#2D3142", "#BFC0C0"],
    ["#1A535C", "#4ECDC4"],
    ["#D62828", "#003049"],
    ["#6A994E", "#BC4749"],
    ["#8B4513", "#D2691E"],
    ["#2C3E50", "#ECF0F1"],
    ["#E63946", "#F1FAEE"]
];

const sizeSets = [
    ["XS", "S", "M"],
    ["S", "M", "L"],
    ["M", "L", "XL"],
    ["XS", "S", "M", "L"],
    ["S", "M", "L", "XL"],
    ["M", "L", "XL", "XXL"]
];

const imageUrls = [
    // T-Shirts & Tops
    "https://images.unsplash.com/photo-1618354691373-d851c5c3a990",
    "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab",
    "https://images.unsplash.com/photo-1576566588028-4147f3842f27",
    "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a",
    "https://images.unsplash.com/photo-1562157873-818bc0726f68",
    "https://images.unsplash.com/photo-1581655353564-df123a1eb820",
    "https://images.unsplash.com/photo-1622470953794-aa9c70b0fb9d",
    "https://images.unsplash.com/photo-1627225924765-552d49cf47ad",
    "https://images.unsplash.com/photo-1574180566232-aaad1b5b8450",
    "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c",

    // Shirts & Dress Shirts
    "https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77",
    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c",
    "https://images.unsplash.com/photo-1620799139507-2a76f79a2f4d",
    "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf",
    "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4",
    "https://images.unsplash.com/photo-1598032895397-90d6b8e211a0",
    "https://images.unsplash.com/photo-1603252109303-2751441dd157",
    "https://images.unsplash.com/photo-1589310243389-96a5483213a8",
    "https://images.unsplash.com/photo-1602810316498-ab67cf68c8e1",
    "https://images.unsplash.com/photo-1596755095030-32b8e2c8d272",

    // Jackets & Outerwear
    "https://images.unsplash.com/photo-1591047139829-d91aecb6caea",
    "https://images.unsplash.com/photo-1591047139902-c0ccc48dc18d",
    "https://images.unsplash.com/photo-1591047139825-ba9e7e7c8f16",
    "https://images.unsplash.com/photo-1594938291221-94f18cbb5660",
    "https://images.unsplash.com/photo-1578681994506-b8f463449011",
    "https://images.unsplash.com/photo-1551028719-00167b16eac5",
    "https://images.unsplash.com/photo-1548126032-079428576a13",
    "https://images.unsplash.com/photo-1539533018447-63fcce2678e3",
    "https://images.unsplash.com/photo-1544022613-e87ca75a784a",
    "https://images.unsplash.com/photo-1591561954557-26941169b49e",

    // Sweaters & Hoodies
    "https://images.unsplash.com/photo-1571945153237-4929e783af4a",
    "https://images.unsplash.com/photo-1586790170083-2f9ceadc732d",
    "https://images.unsplash.com/photo-1525450824786-227cbef70703",
    "https://images.unsplash.com/photo-1620799140777-8ea180229670",
    "https://images.unsplash.com/photo-1556821840-3a63f95609a7",
    "https://images.unsplash.com/photo-1578587018452-892bacefd3f2",
    "https://images.unsplash.com/photo-1564584217132-2271feaeb3c5",
    "https://images.unsplash.com/photo-1620799139834-6b8f844febe5",
    "https://images.unsplash.com/photo-1614252368655-8e0c9e561a63",
    "https://images.unsplash.com/photo-1556905055-8f358a7a47b2",

    // Jeans & Pants
    "https://images.unsplash.com/photo-1542272604-787c3835535d",
    "https://images.unsplash.com/photo-1475178626620-a4d074967452",
    "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80",
    "https://images.unsplash.com/photo-1605518216938-7c31b7b14ad0",
    "https://images.unsplash.com/photo-1541099649105-f69ad21f3246",
    "https://images.unsplash.com/photo-1582552938357-32b906df40cb",
    "https://images.unsplash.com/photo-1604176354204-9268737828e4",
    "https://images.unsplash.com/photo-1598554747436-c9293d6a588f",
    "https://images.unsplash.com/photo-1624378440070-7de4a2a02b2e",
    "https://images.unsplash.com/photo-1473966968600-fa801b869a1a",

    // Shorts
    "https://images.unsplash.com/photo-1591195853828-11db59a44f6b",
    "https://images.unsplash.com/photo-1591195850126-f5e8b4f285d3",
    "https://images.unsplash.com/photo-1591195853650-7b670bd8f4cf",
    "https://images.unsplash.com/photo-1565299585323-38d6b0865b47",
    "https://images.unsplash.com/photo-1519689680058-324335c77eba",
    "https://images.unsplash.com/photo-1598554747418-ccf8d1f17c7e",
    "https://images.unsplash.com/photo-1591195850-67b670d3f2b2",
    "https://images.unsplash.com/photo-1555689502-c4b22d76c56f",
    "https://images.unsplash.com/photo-1599586120429-48281b6f0ece",
    "https://images.unsplash.com/photo-1555689502-c9f1db3a8b3c",

    // Dresses
    "https://images.unsplash.com/photo-1595777457583-95e059d581b8",
    "https://images.unsplash.com/photo-1566174053879-31528523f8ae",
    "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03",
    "https://images.unsplash.com/photo-1612336307429-8a898d10e223",
    "https://images.unsplash.com/photo-1596783074918-c84cb06531ca",
    "https://images.unsplash.com/photo-1572804013427-4d7ca7268217",
    "https://images.unsplash.com/photo-1595777216776-6f9d0e879f8e",
    "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446",
    "https://images.unsplash.com/photo-1496747611176-843222e1e57c",
    "https://images.unsplash.com/photo-1550639525-c97d455acf70",

    // Skirts
    "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa",
    "https://images.unsplash.com/photo-1583496661150-fb5886a0aaaa",
    "https://images.unsplash.com/photo-1594633313593-bab3825d0caf",
    "https://images.unsplash.com/photo-1594633313719-e48e5de0545f",
    "https://images.unsplash.com/photo-1594633313593-bab3825d0caf",
    "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa",
    "https://images.unsplash.com/photo-1594633313597-2e6e572ca5eb",
    "https://images.unsplash.com/photo-1583496661283-21e8b02b4319",
    "https://images.unsplash.com/photo-1583496661306-5ac4f6fadb8e",
    "https://images.unsplash.com/photo-1583496661265-3f1ebe65c0e8",

    // Blazers & Formal
    "https://images.unsplash.com/photo-1507679799987-c73779587ccf",
    "https://images.unsplash.com/photo-1593030668-2ca6c933a013",
    "https://images.unsplash.com/photo-1594938291221-94f18cbb5660",
    "https://images.unsplash.com/photo-1617127365659-c47fa864d8bc",
    "https://images.unsplash.com/photo-1598808503491-c8e0144b0c8e",
    "https://images.unsplash.com/photo-1593032465207-3aa650d8f2f6",
    "https://images.unsplash.com/photo-1608234807831-b8e9a0c7a00c",
    "https://images.unsplash.com/photo-1617127365659-c47fa864d8bc",
    "https://images.unsplash.com/photo-1598808503491-c8e0144b0c8e",
    "https://images.unsplash.com/photo-1594938291221-94f18cbb5660",

    // Additional variety - Mix of styles
    "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f",
    "https://images.unsplash.com/photo-1490481651871-ab68de25d43d",
    "https://images.unsplash.com/photo-1467043237213-65f2da53396f",
    "https://images.unsplash.com/photo-1469334031218-e382a71b716b",
    "https://images.unsplash.com/photo-1445205170230-053b83016050",
    "https://images.unsplash.com/photo-1434389677669-e08b4cac3105",
    "https://images.unsplash.com/photo-1441984904996-e0b6ba687e04",
    "https://images.unsplash.com/photo-1558769132-cb1aea3c672d",
    "https://images.unsplash.com/photo-1509631179647-0177331693ae",
    "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f",

    // More casual wear
    "https://images.unsplash.com/photo-1484327973588-c31f829103fe",
    "https://images.unsplash.com/photo-1516762689617-e1cffcef479d",
    "https://images.unsplash.com/photo-1490481651871-ab68de25d43d",
    "https://images.unsplash.com/photo-1523359346063-d879354c0ea5",
    "https://images.unsplash.com/photo-1525171254930-643fc658b64e"
];

const tags = ["standard", "premium", "casual", "formal", "sport", "trending", "new arrival", "bestseller"];

const careInstructions = [
    "Machine wash cold",
    "Hand wash only",
    "Dry clean only",
    "Machine wash warm",
    "Wash separately",
    "Do not bleach",
    "Iron on low heat"
];

function randomChoice(array) {
    return array[Math.floor(Math.random() * array.length)];
}

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generateProducts(count) {
    const products = [];

    for (let i = 1; i <= count; i++) {
        const productType = randomChoice(productTypes);
        const colorSet = randomChoice(colorSets);
        const sizeSet = randomChoice(sizeSets);

        const imageUrl = imageUrls[(i - 1) % imageUrls.length];

        const product = {
            productCode: `AORI-${productType.code}-${String(i).padStart(3, '0')}`,
            productName: `${productType.name} ${i}`,
            description: `High quality ${productType.name.toLowerCase()} with excellent craftsmanship and comfortable fit`,
            categoryId: randomChoice(categoryIds),
            collection: randomChoice(collections),
            material: randomChoice(materials),
            season: randomChoice(seasons),
            careInstructions: randomChoice(careInstructions),
            colors: JSON.stringify(colorSet),
            image: `${imageUrl}?w=400&h=600&fit=crop`,
            price: randomInt(50, 300),
            stockQuantity: randomInt(10, 100),
            size: JSON.stringify(sizeSet),
            rating: randomInt(1, 5),
            tags: randomChoice(tags)
        };

        products.push(product);
    }

    return products;
}

function main() {
    const args = process.argv.slice(2);
    const command = args[0];
    const count = parseInt(args[1]) || 100;

    if (command === 'generate') {
        console.log(`Generating ${count} products...`);
        const products = generateProducts(count);

        const outputFile = 'products.json';
        fs.writeFileSync(outputFile, JSON.stringify(products, null, 2), 'utf8');

        console.log(`Successfully generated ${products.length} products!`);
        console.log(`Output saved to: ${outputFile}`);

        console.log('\nSample products:');
        console.log(JSON.stringify(products.slice(0, 3), null, 2));
    } else {
        console.log('Usage: node generate_products.js generate [count]');
        console.log('Example: node generate_products.js generate 100');
    }
}

main();
