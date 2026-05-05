function login() {
  console.log("LOGIN CLICKED");

  const username = document.querySelector("input[type='text']").value;
  const password = document.querySelector("input[type='password']").value;

  fetch("http://localhost:8080/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      username: username,
      password: password
    })
  })
  .then(res => res.json())
  .then(data => {
    console.log(data);

    localStorage.setItem("token", data.token);

    window.location.href = "/dashboard.html";
  })
  .catch(err => console.error(err));
}