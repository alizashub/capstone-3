let cartService;

class ShoppingCartService {

    cart = {
        items: [],
        total: 0
    };

    // ---------------- ADD TO CART ----------------
    addToCart(productId) {
        const url = `${config.baseUrl}/cart/products/${productId}`;

        axios.post(url, {})
            .then(response => {
                this.setCart(response.data);
                this.updateCartDisplay();
            })
            .catch(error => {
                templateBuilder.append("error", { error: "Add to cart failed." }, "errors");
            });
    }

    // ---------------- SET CART ----------------
    setCart(data) {
        this.cart = {
            items: [],
            total: data.total
        };

        for (const value of Object.values(data.items)) {
            this.cart.items.push(value);
        }
    }

    // ---------------- LOAD CART ----------------
    loadCart() {
        const url = `${config.baseUrl}/cart`;

        axios.get(url)
            .then(response => {
                this.setCart(response.data);
                this.updateCartDisplay();
            })
            .catch(error => {
                templateBuilder.append("error", { error: "Load cart failed." }, "errors");
            });
    }

    // ---------------- LOAD CART PAGE ----------------
    loadCartPage() {
        const main = document.getElementById("main");
        main.innerHTML = "";

        const contentDiv = document.createElement("div");
        contentDiv.id = "content";
        contentDiv.classList.add("content-form");

        // ---------- HEADER ----------
        const cartHeader = document.createElement("div");
        cartHeader.classList.add("cart-header");

        const h1 = document.createElement("h1");
        h1.innerText = "Cart";

        // ---------- BUTTON GROUP ----------
        const buttonGroup = document.createElement("div");
        buttonGroup.classList.add("cart-actions");

        const clearBtn = document.createElement("button");
        clearBtn.classList.add("btn", "btn-danger");
        clearBtn.innerText = "Clear";
        clearBtn.addEventListener("click", () => this.clearCart());

        const checkoutBtn = document.createElement("button");
        checkoutBtn.classList.add("btn", "btn-primary");
        checkoutBtn.innerText = "Checkout";
        checkoutBtn.addEventListener("click", () => this.checkout());

        buttonGroup.appendChild(clearBtn);
        buttonGroup.appendChild(checkoutBtn);

        cartHeader.appendChild(h1);
        cartHeader.appendChild(buttonGroup);

        contentDiv.appendChild(cartHeader);
        main.appendChild(contentDiv);

        // ---------- CART ITEMS ----------
        this.cart.items.forEach(item => {
            this.buildItem(item, contentDiv);
        });
    }

    // ---------------- CHECKOUT (SHOW TOTAL) ----------------
    checkout() {
        const content = document.getElementById("content");

        // Remove existing total if already shown
        const existingTotal = document.getElementById("checkout-total");
        if (existingTotal) existingTotal.remove();

        const totalDiv = document.createElement("div");
        totalDiv.id = "checkout-total";
        totalDiv.classList.add("cart-total");

        const totalH3 = document.createElement("h3");
        totalH3.innerText = `TOTAL: $${this.cart.total.toFixed(2)}`;

        totalDiv.appendChild(totalH3);
        content.appendChild(totalDiv);
    }

    // ---------------- BUILD CART ITEM ----------------
    buildItem(item, parent) {
        const outerDiv = document.createElement("div");
        outerDiv.classList.add("cart-item");

        const nameDiv = document.createElement("div");
        const h4 = document.createElement("h4");
        h4.innerText = item.product.name;
        nameDiv.appendChild(h4);

        const photoDiv = document.createElement("div");
        photoDiv.classList.add("photo");

        const img = document.createElement("img");
        img.src = `/images/products/${item.product.imageUrl}`;
        img.addEventListener("click", () => {
            showImageDetailForm(item.product.name, img.src);
        });

        const priceH4 = document.createElement("h4");
        priceH4.classList.add("price");
        priceH4.innerText = `$${item.lineTotal.toFixed(2)}`;

        photoDiv.appendChild(img);
        photoDiv.appendChild(priceH4);

        const descriptionDiv = document.createElement("div");
        descriptionDiv.innerText = item.product.description;

        const quantityDiv = document.createElement("div");
        quantityDiv.innerText = `Quantity: ${item.quantity}`;

        outerDiv.appendChild(nameDiv);
        outerDiv.appendChild(photoDiv);
        outerDiv.appendChild(descriptionDiv);
        outerDiv.appendChild(quantityDiv);

        parent.appendChild(outerDiv);
    }

    // ---------------- CLEAR CART ----------------
    clearCart() {
        const url = `${config.baseUrl}/cart`;

        axios.delete(url)
            .then(response => {
                this.setCart(response.data);
                this.updateCartDisplay();
                this.loadCartPage();
            })
            .catch(error => {
                templateBuilder.append("error", { error: "Empty cart failed." }, "errors");
            });
    }

    // ---------------- UPDATE CART COUNT ----------------
    updateCartDisplay() {
        try {
            const cartControl = document.getElementById("cart-items");
            cartControl.innerText = this.cart.items.length;
        } catch (e) {}
    }
}

// ---------------- INIT ----------------
document.addEventListener('DOMContentLoaded', () => {
    cartService = new ShoppingCartService();

    if (userService.isLoggedIn()) {
        cartService.loadCart();
    }
});