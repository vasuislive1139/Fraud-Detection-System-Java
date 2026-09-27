import { AlertTriangle, Clock } from 'lucide-react';

const MOCK_CASES = [
  { id: 'CASE-001', txId: 'tx-88192a', amount: 504200.00, status: 'OPEN', assignee: 'Admin User', rules: ['UNUSUAL_AMOUNT', 'EMPTY_ACCOUNT_TRANSFER'], time: '2 mins ago' },
  { id: 'CASE-002', txId: 'tx-77421c', amount: 25000.00, status: 'NEW', assignee: 'Unassigned', rules: ['HIGH_VELOCITY'], time: '15 mins ago' },
];

const Alerts = () => {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Alerts & Cases</h1>
          <p className="text-gray-400 mt-1">Investigate and resolve flagged transactions</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-navy-800 rounded-xl border border-navy-700 p-6 md:col-span-2">
          <h3 className="text-lg font-medium text-white mb-6">Active Investigations</h3>
          
          <div className="space-y-4">
            {MOCK_CASES.map((c) => (
              <div key={c.id} className="bg-navy-900 border border-navy-700 rounded-lg p-5 flex items-center justify-between hover:border-primary-500/50 transition-colors cursor-pointer group">
                <div className="flex gap-4">
                  <div className={`p-3 rounded-full h-fit ${c.status === 'OPEN' ? 'bg-amber-500/20 text-amber-500' : 'bg-danger-500/20 text-danger-500'}`}>
                    <AlertTriangle className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-3 mb-1">
                      <h4 className="font-bold text-white">{c.id}</h4>
                      <span className={`px-2 py-0.5 text-xs font-semibold rounded ${c.status === 'OPEN' ? 'bg-amber-500 text-black' : 'bg-blue-500 text-white'}`}>
                        {c.status}
                      </span>
                    </div>
                    <p className="text-sm text-gray-400 mb-2">Tx: {c.txId} • ${c.amount.toLocaleString()}</p>
                    <div className="flex gap-2">
                      {c.rules.map(rule => (
                        <span key={rule} className="px-2 py-1 bg-navy-800 text-xs text-gray-300 rounded border border-navy-700">
                          {rule}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
                
                <div className="text-right flex flex-col items-end">
                  <p className="text-sm text-gray-400 flex items-center gap-1 mb-3">
                    <Clock className="w-4 h-4" />
                    {c.time}
                  </p>
                  <p className="text-sm text-gray-300">
                    <span className="text-gray-500 mr-2">Assignee:</span>
                    {c.assignee}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-navy-800 rounded-xl border border-navy-700 p-6">
          <h3 className="text-lg font-medium text-white mb-4">Case Statistics</h3>
          <div className="space-y-4">
            <div className="bg-navy-900 p-4 rounded-lg border border-navy-700 flex justify-between items-center">
              <span className="text-gray-400">Total Open</span>
              <span className="text-xl font-bold text-white">142</span>
            </div>
            <div className="bg-navy-900 p-4 rounded-lg border border-navy-700 flex justify-between items-center">
              <span className="text-gray-400">Unassigned</span>
              <span className="text-xl font-bold text-danger-500">28</span>
            </div>
            <div className="bg-navy-900 p-4 rounded-lg border border-navy-700 flex justify-between items-center">
              <span className="text-gray-400">Resolved Today</span>
              <span className="text-xl font-bold text-success-500">12</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Alerts;
