
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">Pidilite Lanka</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="createProductDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Product
                </a>
                <div class="dropdown-menu" aria-labelledby="createProductDropdown">
                    <a class="dropdown-item" href="/product/create">Create Product</a>
                    <a class="dropdown-item" href="/product">View Products</a>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="createCustomerDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Customer
                </a>
                <div class="dropdown-menu" aria-labelledby="createCustomerDropdown">
                    <a class="dropdown-item" href="/customer/create">Create Customer</a>
                    <a class="dropdown-item" href="/customer">View Customers</a>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="createUserDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    User
                </a>
                <div class="dropdown-menu" aria-labelledby="createUserDropdown">
                    <a class="dropdown-item" href="/user/create">Create User</a>
                    <a class="dropdown-item" href="/user">View Users</a>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="createMessageDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Messages
                </a>
                <div class="dropdown-menu" aria-labelledby="createMessageDropdown">
                    <a class="dropdown-item" href="/message/create">Create Bulk Message</a>
                    <a class="dropdown-item" href="/message">View Messages</a>
                    <a class="dropdown-item" href="/message/received">View Received Messages</a>
                </div>
            </li>
            <li class="nav-item">
                    <a class="nav-link" href="/redeem">Redeem</a>

            </li>

        </ul>
        <div class="form-inline my-2 my-lg-0">
            <a class="nav-link" href="/logout">Logout</a>
        </div>
    </div>
    <%--<div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="/product">All Products</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/product/create">Create Product</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/customer">All Customers</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/customer/create">Create Customer</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/user">All Users</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/user/create">Create User</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/message">Messages</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/message/create">Bulk Message</a>
            </li>

        </ul>
        <div class="form-inline my-2 my-lg-0 ">
            <a class="nav-link" href="/logout">Logout</a>
        </div>
    </div>--%>

</nav>

