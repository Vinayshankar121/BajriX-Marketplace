import React from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';

const API_URL = 'http://localhost:8081/api';

function App() {
  const [activePage, setActivePage] = React.useState('buyer');
  const [products, setProducts] = React.useState([]);
  const [pageInfo, setPageInfo] = React.useState({ number: 0, totalPages: 0, first: true, last: true });
  const [searchText, setSearchText] = React.useState('');
  const [selectedProduct, setSelectedProduct] = React.useState(null);
  const [offers, setOffers] = React.useState([]);
  const [sellerId, setSellerId] = React.useState('1');
  const [adminSellers, setAdminSellers] = React.useState([]);
  const [message, setMessage] = React.useState('');

  React.useEffect(function () {
    loadProducts('', 0);
    loadAdminSellers();
  }, []);

  async function loadProducts(value, page) {
    try {
      const response = await fetch(API_URL + '/buyer/products?search=' + encodeURIComponent(value) + '&page=' + page + '&size=10');
      if (!response.ok) {
        setProducts([]);
        setPageInfo({ number: 0, totalPages: 0, first: true, last: true });
        setMessage('Could not load products. Please check the backend server.');
        return;
      }

      const data = await response.json();
      setProducts(Array.isArray(data.content) ? data.content : []);
      setPageInfo({
        number: data.number,
        totalPages: data.totalPages,
        first: data.first,
        last: data.last
      });
    } catch (error) {
      setProducts([]);
      setPageInfo({ number: 0, totalPages: 0, first: true, last: true });
      setMessage('Could not connect to the backend server.');
    }
  }

  async function loadAdminSellers() {
    try {
      const response = await fetch(API_URL + '/admin/sellers');
      if (!response.ok) {
        setMessage('Could not load the seller approval list.');
        return;
      }

      const data = await response.json();
      setAdminSellers(Array.isArray(data) ? data : []);
    } catch (error) {
      setMessage('Could not connect to the backend while loading sellers.');
    }
  }

  async function searchProducts() {
    setSelectedProduct(null);
    setOffers([]);
    await loadProducts(searchText, 0);
  }

  async function showOffers(product) {
    setSelectedProduct(product);
    try {
      const response = await fetch(API_URL + '/buyer/products/' + product.id + '/offers');
      if (!response.ok) {
        setOffers([]);
        setMessage('Could not load seller offers.');
        return;
      }

      const data = await response.json();
      setOffers(Array.isArray(data) ? data : []);
    } catch (error) {
      setOffers([]);
      setMessage('Could not connect to the backend server.');
    }
  }

  async function updateSellerStatus(sellerIdValue, status) {
    try {
      const response = await fetch(API_URL + '/admin/sellers/' + sellerIdValue + '/status', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
      });

      if (!response.ok) {
        const error = await response.json();
        setMessage(error.message || 'Could not update seller status.');
        return;
      }

      setMessage('Seller status updated successfully.');
      await loadAdminSellers();
    } catch (error) {
      setMessage('Could not update seller status.');
    }
  }

  function changePage(pageName) {
    setActivePage(pageName);
    setMessage('');
  }

  return (
    <main>
      <header>
        <div>
          <h1>BajriX Marketplace</h1>
          <p>Compare trustworthy local suppliers for construction essentials</p>
        </div>

        <nav>
          <button className={activePage === 'buyer' ? 'active' : ''} onClick={function () { changePage('buyer'); }}>
            Buy products
          </button>
          <button className={activePage === 'seller' ? 'active' : ''} onClick={function () { changePage('seller'); }}>
            Seller dashboard
          </button>
          <button className={activePage === 'admin' ? 'active' : ''} onClick={function () { changePage('admin'); }}>
            Seller approval
          </button>
        </nav>
      </header>

      {message && <div className="message">{message}</div>}

      {activePage === 'buyer' && (
        <BuyerPage
          products={products}
          pageInfo={pageInfo}
          goToPage={function (page) { loadProducts(searchText, page); }}
          searchText={searchText}
          setSearchText={setSearchText}
          searchProducts={searchProducts}
          selectedProduct={selectedProduct}
          setSelectedProduct={setSelectedProduct}
          offers={offers}
          showOffers={showOffers}
        />
      )}

      {activePage === 'seller' && (
        <SellerPage
          products={products}
          sellerId={sellerId}
          setSellerId={setSellerId}
          setMessage={setMessage}
          adminSellers={adminSellers}
          refreshAdminList={loadAdminSellers}
        />
      )}

      {activePage === 'admin' && (
        <AdminPage sellers={adminSellers} updateSellerStatus={updateSellerStatus} />
      )}
    </main>
  );
}

function BuyerPage(props) {
  function handleKeyDown(event) {
    if (event.key === 'Enter') {
      props.searchProducts();
    }
  }

  const categories = Array.from(new Set(props.products.map((product) => product.category).filter(Boolean)));
  const [selectedCategory, setSelectedCategory] = React.useState('All');

  const filteredProducts = props.products.filter((product) => {
    if (selectedCategory === 'All') {
      return true;
    }
    return product.category === selectedCategory;
  });

  return (
    <section>
      <div className="search-wrap">
        <div className="search">
          <input
            value={props.searchText}
            placeholder="Search cement, steel, paint, sand..."
            onChange={function (event) { props.setSearchText(event.target.value); }}
            onKeyDown={handleKeyDown}
          />
          <button onClick={props.searchProducts}>Search</button>
        </div>

        <div className="chip-row">
          <button className={selectedCategory === 'All' ? 'chip active' : 'chip'} onClick={function () { setSelectedCategory('All'); }}>
            All
          </button>
          {categories.map((category) => (
            <button
              key={category}
              className={selectedCategory === category ? 'chip active' : 'chip'}
              onClick={function () { setSelectedCategory(category); }}
            >
              {category}
            </button>
          ))}
        </div>
      </div>

      <div className="layout">
        <div className="cards">
          {filteredProducts.length === 0 && <div className="empty-state">No products match this search.</div>}
          {filteredProducts.map(function (product) {
            return (
              <article className="card" key={product.id}>
                <span>{product.category || 'General'}</span>
                <h3>{product.name}</h3>
                <p>{product.description || 'Construction essentials and building materials.'}</p>
                <div className="meta-row">
                  <small>Unit: {product.unit || 'item'}</small>
                  <button onClick={function () { props.showOffers(product); }}>Compare sellers</button>
                </div>
              </article>
            );
          })}
          {props.pageInfo.totalPages > 1 && (
            <div className="pagination">
              <button
                className="secondary"
                disabled={props.pageInfo.first}
                onClick={function () { props.goToPage(props.pageInfo.number - 1); }}
              >
                Previous
              </button>
              <span>Page {props.pageInfo.number + 1} of {props.pageInfo.totalPages}</span>
              <button
                className="secondary"
                disabled={props.pageInfo.last}
                onClick={function () { props.goToPage(props.pageInfo.number + 1); }}
              >
                Next
              </button>
            </div>
          )}
        </div>

        <OffersPanel product={props.selectedProduct} offers={props.offers} />
      </div>
    </section>
  );
}

function OffersPanel(props) {
  if (props.product === null) {
    return (
      <aside>
        <h2>Compare offers</h2>
        <p>Select a product to see the best current bids from approved sellers.</p>
      </aside>
    );
  }

  const sortedOffers = [...props.offers].sort((a, b) => Number(a.price) - Number(b.price));
  const cheapest = sortedOffers[0];

  return (
    <aside>
      <h2>{props.product.name}</h2>
      <p>Approved seller offers</p>

      {sortedOffers.length === 0 && <p>No approved sellers currently have this product in stock.</p>}

      {cheapest && (
        <div className="best-offer-box">
          <span>Best offer</span>
          <strong>₹{cheapest.price}</strong>
          <small>from {cheapest.sellerName}</small>
        </div>
      )}

      {sortedOffers.map(function (offer) {
        return (
          <div className="offer" key={offer.id}>
            <strong>{offer.sellerName}</strong>
            <b>₹{offer.price} / {props.product.unit || 'unit'}</b>
            <small>Stock: {offer.stock} · Min order: {offer.minimumOrderQuantity}</small>
          </div>
        );
      })}
    </aside>
  );
}

function SellerPage(props) {
  const [listings, setListings] = React.useState([]);
  const [form, setForm] = React.useState(createEmptyForm());
  const [editingId, setEditingId] = React.useState(null);

  React.useEffect(function () {
    loadListings();
  }, [props.sellerId]);

  const currentSeller = (props.adminSellers || []).find((seller) => String(seller.id) === String(props.sellerId));

  function createEmptyForm() {
    return {
      productId: '',
      price: '',
      stock: '',
      minimumOrderQuantity: '',
      available: true
    };
  }

  async function loadListings() {
    try {
      const response = await fetch(API_URL + '/seller/listings', {
        headers: { 'X-Seller-Id': props.sellerId }
      });

      if (response.ok) {
        const data = await response.json();
        setListings(Array.isArray(data) ? data : []);
        return;
      }

      const error = await response.json();
      props.setMessage(error.message || 'Could not load this seller’s listings.');
    } catch (error) {
      props.setMessage('Could not connect to the backend while loading listings.');
    }
  }

  function updateForm(event) {
    const name = event.target.name;
    const value = event.target.value;
    setForm({ ...form, [name]: value });
  }

  function updateAvailable(event) {
    setForm({ ...form, available: event.target.checked });
  }

  function startEditing(listing) {
    setEditingId(listing.id);
    setForm({
      productId: String(listing.productId),
      price: listing.price,
      stock: listing.stock,
      minimumOrderQuantity: listing.minimumOrderQuantity,
      available: listing.available
    });
  }

  function cancelEditing() {
    setEditingId(null);
    setForm(createEmptyForm());
  }

  async function saveListing(event) {
    event.preventDefault();

    const data = {
      productId: Number(form.productId),
      price: Number(form.price),
      stock: Number(form.stock),
      minimumOrderQuantity: Number(form.minimumOrderQuantity),
      available: form.available
    };

    let url = API_URL + '/seller/listings';
    let method = 'POST';

    if (editingId !== null) {
      url = API_URL + '/seller/listings/' + editingId;
      method = 'PUT';
    }

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'X-Seller-Id': props.sellerId
        },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        props.setMessage('Listing saved successfully.');
        cancelEditing();
        loadListings();
      } else {
        const error = await response.json();
        props.setMessage(error.message || 'Could not save listing.');
      }
    } catch (error) {
      props.setMessage('Could not connect to the backend.');
    }
  }

  async function stopSelling(listingId) {
    const confirmed = window.confirm('Stop selling this product?');
    if (!confirmed) {
      return;
    }

    try {
      const response = await fetch(API_URL + '/seller/listings/' + listingId, {
        method: 'DELETE',
        headers: { 'X-Seller-Id': props.sellerId }
      });

      if (response.ok) {
        props.setMessage('Listing is now unavailable.');
        loadListings();
        return;
      }

      const error = await response.json();
      props.setMessage(error.message || 'Could not stop selling this listing.');
    } catch (error) {
      props.setMessage('Could not stop selling this listing.');
    }
  }

  return (
    <section>
      <div className="seller-head">
        <div>
          <h2>My catalogue</h2>
          <p>Manage your product price, stock and minimum order.</p>
        </div>

        <div className="seller-actions">
          <label>
            Current seller
            <select value={props.sellerId} onChange={function (event) { props.setSellerId(event.target.value); }}>
              {props.adminSellers.map((seller) => (
                <option key={seller.id} value={seller.id}>{seller.name}</option>
              ))}
            </select>
          </label>
          {currentSeller && (
            <span className={'status-badge ' + currentSeller.status.toLowerCase()}>{currentSeller.status}</span>
          )}
        </div>
      </div>

      <div className="seller-layout">
        <ListingForm
          form={form}
          editingId={editingId}
          products={props.products}
          updateForm={updateForm}
          updateAvailable={updateAvailable}
          saveListing={saveListing}
          cancelEditing={cancelEditing}
        />
        <ListingList listings={listings} startEditing={startEditing} stopSelling={stopSelling} />
      </div>
    </section>
  );
}

function ListingForm(props) {
  return (
    <form onSubmit={props.saveListing}>
      <h3>{props.editingId === null ? 'Add product listing' : 'Edit listing'}</h3>

      <label>Product
        <select name="productId" required value={props.form.productId} disabled={props.editingId !== null} onChange={props.updateForm}>
          <option value="">Choose a product</option>
          {props.products.map(function (product) { return <option key={product.id} value={product.id}>{product.name}</option>; })}
        </select>
      </label>

      <label>Price (₹)
        <input name="price" type="number" min="0.01" step="0.01" required value={props.form.price} onChange={props.updateForm} />
      </label>

      <label>Stock
        <input name="stock" type="number" min="0" required value={props.form.stock} onChange={props.updateForm} />
      </label>

      <label>Minimum order quantity
        <input name="minimumOrderQuantity" type="number" min="1" required value={props.form.minimumOrderQuantity} onChange={props.updateForm} />
      </label>

      <label className="check">
        <input type="checkbox" checked={props.form.available} onChange={props.updateAvailable} />
        Available to buyers
      </label>

      <button>{props.editingId === null ? 'Add listing' : 'Update listing'}</button>
      {props.editingId !== null && <button className="secondary" type="button" onClick={props.cancelEditing}>Cancel</button>}
    </form>
  );
}

function ListingList(props) {
  return (
    <div className="table">
      {props.listings.length === 0 && <p className="empty-listing">No listings yet for this seller.</p>}
      {props.listings.map(function (listing) {
        return (
          <div className="row" key={listing.id}>
            <div>
              <strong>{listing.productName}</strong>
              <p>₹{listing.price} · Stock {listing.stock} · MOQ {listing.minimumOrderQuantity}</p>
              <small className={listing.available ? 'available' : 'stopped'}>{listing.available ? 'Available' : 'Stopped'}</small>
            </div>
            <div className="row-actions">
              <button className="secondary" onClick={function () { props.startEditing(listing); }}>Edit</button>
              {listing.available && <button className="danger" onClick={function () { props.stopSelling(listing.id); }}>Stop</button>}
            </div>
          </div>
        );
      })}
    </div>
  );
}

function AdminPage(props) {
  const counts = {
    approved: props.sellers.filter((seller) => seller.status === 'APPROVED').length,
    pending: props.sellers.filter((seller) => seller.status === 'PENDING').length,
    rejected: props.sellers.filter((seller) => seller.status === 'REJECTED').length
  };

  return (
    <section className="admin-panel">
      <div className="admin-summary">
        <div className="summary-card approved">
          <span>Approved</span>
          <strong>{counts.approved}</strong>
        </div>
        <div className="summary-card pending">
          <span>Pending</span>
          <strong>{counts.pending}</strong>
        </div>
        <div className="summary-card rejected">
          <span>Rejected</span>
          <strong>{counts.rejected}</strong>
        </div>
      </div>

      <div className="table admin-table">
        <h3>Seller approval queue</h3>
        {props.sellers.map((seller) => (
          <div className="row seller-row" key={seller.id}>
            <div>
              <strong>{seller.name}</strong>
              <p>{seller.listingCount} listings</p>
            </div>

            <div className="status-control">
              <span className={'status-badge ' + seller.status.toLowerCase()}>{seller.status}</span>
              <div className="status-buttons">
                <button className="secondary" onClick={function () { props.updateSellerStatus(seller.id, 'APPROVED'); }}>Approve</button>
                <button className="secondary" onClick={function () { props.updateSellerStatus(seller.id, 'PENDING'); }}>Pending</button>
                <button className="danger" onClick={function () { props.updateSellerStatus(seller.id, 'REJECTED'); }}>Reject</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

const root = createRoot(document.getElementById('root'));
root.render(<App />);
