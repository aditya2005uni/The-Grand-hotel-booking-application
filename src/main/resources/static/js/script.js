// Utility: get JWT from localStorage
function getToken() {
    return localStorage.getItem("jwt");
}

// Signup
async function signup(event) {
    event.preventDefault();
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;

    const res = await fetch("/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (res.ok) {
        document.getElementById("signupMsg").innerText = "Signup successful!";
    } else {
        document.getElementById("signupMsg").innerText = "Signup failed!";
    }
}

// Login
async function login(event) {
    event.preventDefault();
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const res = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (res.ok) {
        const data = await res.json();
        localStorage.setItem("jwt", data.token);
        document.getElementById("loginMsg").innerText = "Login successful!";
    } else {
        document.getElementById("loginMsg").innerText = "Login failed!";
    }
}

// Book room
async function bookRoom(event) {
    event.preventDefault();
    const checkInDate = document.getElementById("checkInDate").value;
    const checkOutDate = document.getElementById("checkOutDate").value;

    const res = await fetch("/bookings", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getToken()
        },
        body: JSON.stringify({ checkInDate, checkOutDate })
    });

    if (res.ok) {
        document.getElementById("bookingMsg").innerText = "Booking successful!";
    } else {
        document.getElementById("bookingMsg").innerText = "Booking failed!";
    }
}
