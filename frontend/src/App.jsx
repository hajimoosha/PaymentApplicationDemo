import React, { useEffect, useState } from 'react';

export default function App() {
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('AUD');
  const [payerName, setPayerName] = useState('');
  const [payerEmail, setPayerEmail] = useState('');
  const [response, setResponse] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [payments, setPayments] = useState([]);
  const [loadingPayments, setLoadingPayments] = useState(false);

  const BASE = 'http://localhost:8081/api/payments';

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    setLoadingPayments(true);
    setError(null);
    try {
      const res = await fetch(BASE);
      if (!res.ok) throw new Error('Failed to load payments');
      const data = await res.json();
      setPayments(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoadingPayments(false);
    }
  };

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    setResponse(null);
    setLoading(true);
    try {
      const body = {
        amount: parseFloat(amount),
        currency,
        payerName,
        payerEmail,
      };

      const res = await fetch(BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Request failed');
      setResponse({ status: res.status, body: data });
      // refresh list
      fetchPayments();
      // clear form
      setAmount('');
      setPayerName('');
      setPayerEmail('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const deletePayment = async (id) => {
    if (!window.confirm('Delete payment ' + id + '?')) return;
    setError(null);
    try {
      const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Delete failed');
      // refresh list
      fetchPayments();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
      <h1>Payments demo</h1>
      <div style={{ display: 'flex', gap: 24 }}>
        <div style={{ minWidth: 360 }}>
          <h2>Create payment</h2>
          <form onSubmit={submit} style={{ maxWidth: 400 }}>
            <div style={{ marginBottom: 8 }}>
              <label>Amount</label>
              <br />
              <input
                type="number"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                required
                placeholder="e.g. 12.34"
                style={{ width: '100%', padding: 8 }}
              />
            </div>

            <div style={{ marginBottom: 8 }}>
              <label>Currency</label>
              <br />
              <input
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                style={{ width: '100%', padding: 8 }}
                placeholder="USD"
              />
            </div>

            <div style={{ marginBottom: 8 }}>
              <label>Payer name</label>
              <br />
              <input
                value={payerName}
                onChange={(e) => setPayerName(e.target.value)}
                style={{ width: '100%', padding: 8 }}
                placeholder="Full name"
              />
            </div>

            <div style={{ marginBottom: 8 }}>
              <label>Payer email</label>
              <br />
              <input
                value={payerEmail}
                onChange={(e) => setPayerEmail(e.target.value)}
                style={{ width: '100%', padding: 8 }}
                placeholder="email@example.com"
              />
            </div>

            <button type="submit" disabled={loading} style={{ padding: '8px 12px' }}>
              {loading ? 'Sending...' : 'Create Payment'}
            </button>
          </form>

          <div style={{ marginTop: 20 }}>
            <h2>Result</h2>
            {error && <div style={{ color: 'crimson' }}>Error: {error}</div>}
            {response && (
              <pre style={{ background: '#f6f6f6', padding: 12 }}>{JSON.stringify(response, null, 2)}</pre>
            )}
          </div>
        </div>

        <div style={{ flex: 1 }}>
          <h2>Payments</h2>
          {loadingPayments ? (
            <div>Loading payments...</div>
          ) : payments.length === 0 ? (
            <div>No payments yet</div>
          ) : (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: 8 }}>ID</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Amount</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Currency</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Status</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Provider Tx</th>
                  <th style={{ textAlign: 'left', padding: 8 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {payments.map((p) => (
                  <tr key={p.id} style={{ borderTop: '1px solid #eee' }}>
                    <td style={{ padding: 8 }}>{p.id}</td>
                    <td style={{ padding: 8 }}>{p.amount}</td>
                    <td style={{ padding: 8 }}>{p.currency}</td>
                    <td style={{ padding: 8 }}>{p.status}</td>
                    <td style={{ padding: 8 }}>{p.providerTransactionId || '-'}</td>
                    <td style={{ padding: 8 }}>
                      <button onClick={() => deletePayment(p.id)} style={{ marginRight: 8 }}>
                        Delete
                      </button>
                      <button
                        onClick={() => setResponse({ status: 200, body: p })}
                        title="View raw"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
