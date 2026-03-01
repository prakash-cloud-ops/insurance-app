import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { policyAPI } from '../api';
import toast from 'react-hot-toast';

export default function Policies() {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState({
    status: '',
    type: '',
    search: '',
  });
  
  const navigate = useNavigate();

  useEffect(() => {
    fetchPolicies();
  }, [page, filters]);

  const fetchPolicies = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: 10,
        ...(filters.status && { status: filters.status }),
        ...(filters.type && { type: filters.type }),
        ...(filters.search && { search: filters.search }),
      };
      const response = await policyAPI.getAll(params);
      setPolicies(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      toast.error('Failed to fetch policies');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to cancel this policy?')) {
      try {
        await policyAPI.delete(id);
        toast.success('Policy cancelled successfully');
        fetchPolicies();
      } catch (error) {
        toast.error('Failed to cancel policy');
      }
    }
  };

  const getStatusBadge = (status) => {
    const colors = {
      ACTIVE: 'bg-green-600 text-white',
      EXPIRED: 'bg-red-600 text-white',
      PENDING: 'bg-yellow-600 text-white',
      CANCELLED: 'bg-gray-600 text-white',
    };
    return (
      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${colors[status]}`}>
        {status}
      </span>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-slate-100">Policies</h1>
        <Link
          to="/policies/create"
          className="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg font-semibold transition"
        >
          Create Policy
        </Link>
      </div>

      <div className="bg-slate-800 rounded-lg p-6 shadow-lg border border-slate-700">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Search by Holder</label>
            <input
              type="text"
              placeholder="Search holder name..."
              value={filters.search}
              onChange={(e) => {
                setFilters({ ...filters, search: e.target.value });
                setPage(0);
              }}
              className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Filter by Status</label>
            <select
              value={filters.status}
              onChange={(e) => {
                setFilters({ ...filters, status: e.target.value });
                setPage(0);
              }}
              className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="EXPIRED">Expired</option>
              <option value="PENDING">Pending</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>
          
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Filter by Type</label>
            <select
              value={filters.type}
              onChange={(e) => {
                setFilters({ ...filters, type: e.target.value });
                setPage(0);
              }}
              className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
            >
              <option value="">All Types</option>
              <option value="HEALTH">Health</option>
              <option value="LIFE">Life</option>
              <option value="VEHICLE">Vehicle</option>
              <option value="PROPERTY">Property</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-8 text-slate-400">Loading...</div>
        ) : policies.length === 0 ? (
          <div className="text-center py-8 text-slate-400">No policies found</div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-700">
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Policy Number</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Holder Name</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Type</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Status</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Premium</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">End Date</th>
                    <th className="text-left py-3 px-4 text-slate-300 font-semibold">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {policies.map((policy) => (
                    <tr key={policy.id} className="border-b border-slate-700 hover:bg-slate-700">
                      <td className="py-3 px-4 text-slate-200">{policy.policyNumber}</td>
                      <td className="py-3 px-4 text-slate-200">{policy.holderName}</td>
                      <td className="py-3 px-4 text-slate-200">{policy.type}</td>
                      <td className="py-3 px-4">{getStatusBadge(policy.status)}</td>
                      <td className="py-3 px-4 text-slate-200">${policy.premiumAmount.toLocaleString()}</td>
                      <td className="py-3 px-4 text-slate-200">{new Date(policy.endDate).toLocaleDateString()}</td>
                      <td className="py-3 px-4">
                        <div className="flex space-x-2">
                          <button
                            onClick={() => navigate(`/policies/edit/${policy.id}`)}
                            className="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDelete(policy.id)}
                            className="px-3 py-1 bg-red-600 hover:bg-red-700 text-white rounded text-sm transition"
                          >
                            Cancel
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {totalPages > 1 && (
              <div className="flex justify-center items-center space-x-2 mt-6">
                <button
                  onClick={() => setPage(page - 1)}
                  disabled={page === 0}
                  className="px-4 py-2 bg-slate-700 text-slate-200 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-600 transition"
                >
                  Previous
                </button>
                <span className="text-slate-300">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  onClick={() => setPage(page + 1)}
                  disabled={page >= totalPages - 1}
                  className="px-4 py-2 bg-slate-700 text-slate-200 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-600 transition"
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
