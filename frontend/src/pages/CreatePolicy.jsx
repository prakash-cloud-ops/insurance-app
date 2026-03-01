import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { policyAPI } from '../api';
import toast from 'react-hot-toast';

export default function CreatePolicy() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    holderName: '',
    type: 'HEALTH',
    status: 'PENDING',
    premiumAmount: '',
    startDate: '',
    endDate: '',
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (new Date(formData.endDate) <= new Date(formData.startDate)) {
      toast.error('End date must be after start date');
      return;
    }

    setLoading(true);
    try {
      await policyAPI.create(formData);
      toast.success('Policy created successfully');
      navigate('/policies');
    } catch (error) {
      toast.error('Failed to create policy');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <button
          onClick={() => navigate('/policies')}
          className="text-slate-400 hover:text-slate-200"
        >
          ← Back
        </button>
        <h1 className="text-3xl font-bold text-slate-100">Create Policy</h1>
      </div>

      <div className="bg-slate-800 rounded-lg p-6 shadow-lg border border-slate-700">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Holder Name *
              </label>
              <input
                type="text"
                name="holderName"
                required
                value={formData.holderName}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
                placeholder="Enter holder name"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Policy Type *
              </label>
              <select
                name="type"
                required
                value={formData.type}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
              >
                <option value="HEALTH">Health</option>
                <option value="LIFE">Life</option>
                <option value="VEHICLE">Vehicle</option>
                <option value="PROPERTY">Property</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Status *
              </label>
              <select
                name="status"
                required
                value={formData.status}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
              >
                <option value="ACTIVE">Active</option>
                <option value="PENDING">Pending</option>
                <option value="EXPIRED">Expired</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Premium Amount *
              </label>
              <input
                type="number"
                name="premiumAmount"
                required
                min="0.01"
                step="0.01"
                value={formData.premiumAmount}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
                placeholder="Enter premium amount"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Start Date *
              </label>
              <input
                type="date"
                name="startDate"
                required
                value={formData.startDate}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                End Date *
              </label>
              <input
                type="date"
                name="endDate"
                required
                value={formData.endDate}
                onChange={handleChange}
                className="w-full px-4 py-2 rounded-lg bg-slate-700 border border-slate-600 text-slate-200"
              />
            </div>
          </div>

          <div className="flex justify-end space-x-4">
            <button
              type="button"
              onClick={() => navigate('/policies')}
              className="px-6 py-2 bg-slate-700 hover:bg-slate-600 text-slate-200 rounded-lg font-semibold transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-6 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg font-semibold transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Creating...' : 'Create Policy'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
