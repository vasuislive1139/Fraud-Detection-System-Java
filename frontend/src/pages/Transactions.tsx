import { useEffect, useState, useRef } from 'react';
import { Activity, ShieldCheck, Search, ShieldAlert, Ban } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

interface TxEvent {
  id: string;
  txId: string;
  type: string;
  amount: number;
  nameOrig: string;
  nameDest: string;
  category: string;
  totalScore: number;
  triggeredRules: string;
}

const Transactions = () => {
  const [isLive, setIsLive] = useState(false);
  
  // Separate buffers for routing
  const approvedBuffer = useRef<TxEvent[]>([]);
  const reviewBuffer = useRef<TxEvent[]>([]);
  const blockedBuffer = useRef<TxEvent[]>([]);

  const [approvedTx, setApprovedTx] = useState<TxEvent[]>([]);
  const [reviewTx, setReviewTx] = useState<TxEvent[]>([]);
  const [blockedTx, setBlockedTx] = useState<TxEvent[]>([]);

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8080/api/v1/simulation/stream');

    eventSource.addEventListener('transaction', (e) => {
      const data: TxEvent = JSON.parse(e.data);
      if (!isLive) setIsLive(true);

      if (data.triggeredRules.includes("ACTION:BLOCKED_HIGH_PRIORITY")) {
        blockedBuffer.current.push(data);
      } else if (data.triggeredRules.includes("ACTION:MANUAL_REVIEW")) {
        reviewBuffer.current.push(data);
      } else {
        approvedBuffer.current.push(data);
      }
    });

    eventSource.onerror = () => setIsLive(false);

    // Throttle UI updates (Flush buffer every 500ms for massive TPS performance)
    const interval = setInterval(() => {
      if (approvedBuffer.current.length > 0) {
        setApprovedTx(prev => [...approvedBuffer.current.reverse(), ...prev].slice(0, 30));
        approvedBuffer.current = [];
      }
      if (reviewBuffer.current.length > 0) {
        setReviewTx(prev => [...reviewBuffer.current.reverse(), ...prev].slice(0, 30));
        reviewBuffer.current = [];
      }
      if (blockedBuffer.current.length > 0) {
        setBlockedTx(prev => [...blockedBuffer.current.reverse(), ...prev].slice(0, 30));
        blockedBuffer.current = [];
      }
    }, 500); 

    return () => {
      eventSource.close();
      clearInterval(interval);
    };
  }, [isLive]);

  const TxCard = ({ tx, type }: { tx: TxEvent, type: 'approved'|'review'|'blocked' }) => {
    const colors = {
      approved: "border-emerald-500/30 bg-emerald-500/10 text-emerald-400",
      review: "border-amber-500/40 bg-amber-500/10 text-amber-400",
      blocked: "border-red-500/50 bg-red-500/10 text-red-400 shadow-[0_0_10px_rgba(239,68,68,0.2)]"
    };

    return (
      <motion.div 
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        className={`p-3 mb-2 rounded-lg border ${colors[type]} flex flex-col gap-1 text-sm font-mono`}
      >
        <div className="flex justify-between items-center">
          <span className="font-bold">${tx.amount?.toLocaleString(undefined, { minimumFractionDigits: 2 })}</span>
          <span className="text-xs opacity-75">{tx.txId?.substring(0, 8)}...</span>
        </div>
        <div className="text-xs flex justify-between items-center mt-1 text-gray-300">
          <span>{tx.type}</span>
          <span className="opacity-60">{tx.nameOrig} → {tx.nameDest}</span>
        </div>
        {type !== 'approved' && (
          <div className="mt-2 text-[10px] text-white/70 bg-black/40 p-1.5 rounded truncate">
            {tx.triggeredRules.replace(/ACTION:[^,]+,?/g, '')}
          </div>
        )}
      </motion.div>
    );
  };

  return (
    <div className="space-y-6 h-full flex flex-col">
      <div className="flex justify-between items-center mb-2">
        <div>
          <h1 className="text-3xl font-bold text-white flex items-center gap-3">
            <Activity className="w-8 h-8 text-cyan-400" />
            Live Routing Matrix
          </h1>
          <p className="text-gray-400 mt-1">Real-time isolation and categorization (Streaming PaySim Dataset)</p>
        </div>
        <div className="flex gap-3 items-center">
          {isLive ? (
            <div className="flex items-center gap-2 px-4 py-2 bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 rounded-lg shadow-[0_0_15px_rgba(16,185,129,0.2)]">
              <Activity className="w-5 h-5 animate-pulse" />
              <span className="font-bold tracking-widest">LIVE DATA FEED</span>
            </div>
          ) : (
            <div className="px-4 py-2 bg-gray-800 text-gray-400 rounded-lg">OFFLINE</div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 flex-1 min-h-[70vh]">
        {/* Approved Column */}
        <div className="bg-[#0f172a] rounded-xl border border-[#1e293b] flex flex-col overflow-hidden">
          <div className="p-4 border-b border-[#1e293b] bg-emerald-500/5 flex items-center justify-between">
            <h2 className="text-emerald-400 font-bold flex items-center gap-2 uppercase tracking-wider">
              <ShieldCheck className="w-5 h-5" /> Auto-Approved
            </h2>
            <span className="text-xs bg-emerald-500/20 px-2 py-1 rounded text-emerald-300">{approvedTx.length}+ / sec</span>
          </div>
          <div className="p-4 flex-1 overflow-y-auto">
            <AnimatePresence>
              {approvedTx.map(tx => <TxCard key={tx.id || Math.random()} tx={tx} type="approved" />)}
            </AnimatePresence>
          </div>
        </div>

        {/* Manual Review Column */}
        <div className="bg-[#0f172a] rounded-xl border border-[#1e293b] flex flex-col overflow-hidden">
          <div className="p-4 border-b border-[#1e293b] bg-amber-500/5 flex items-center justify-between">
            <h2 className="text-amber-400 font-bold flex items-center gap-2 uppercase tracking-wider">
              <Search className="w-5 h-5" /> Checking Phase
            </h2>
          </div>
          <div className="p-4 flex-1 overflow-y-auto">
            <AnimatePresence>
              {reviewTx.map(tx => <TxCard key={tx.id || Math.random()} tx={tx} type="review" />)}
            </AnimatePresence>
          </div>
        </div>

        {/* Blocked / High Risk Column */}
        <div className="bg-[#0f172a] rounded-xl border border-[#1e293b] flex flex-col overflow-hidden relative shadow-[0_0_30px_rgba(239,68,68,0.05)]">
          <div className="absolute top-0 inset-x-0 h-1 bg-red-500"></div>
          <div className="p-4 border-b border-[#1e293b] bg-red-500/10 flex items-center justify-between">
            <h2 className="text-red-500 font-bold flex items-center gap-2 uppercase tracking-wider">
              <Ban className="w-5 h-5" /> Blocked (High Priority)
            </h2>
          </div>
          <div className="p-4 flex-1 overflow-y-auto">
            <AnimatePresence>
              {blockedTx.map(tx => <TxCard key={tx.id || Math.random()} tx={tx} type="blocked" />)}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Transactions;
