import { useEffect, useState } from 'react';
import { ResponsiveContainer, AreaChart, Area, Tooltip } from 'recharts';
import { Activity, ShieldAlert, Zap, Globe } from 'lucide-react';
import { motion } from 'framer-motion';
import DraggableWidgetGrid, { type WidgetItem } from '../components/ui/draggable-widget-grid';

// --- WIDGET DEFINITIONS ---
type Kind = 'velocity' | 'processed' | 'critical' | 'anomalies' | 'radar';

interface DashboardWidget extends WidgetItem {
  kind: Kind;
}

const WIDGETS: DashboardWidget[] = [
  { id: 'radar', kind: 'radar', size: 'lg', label: 'Global Network Radar' },
  { id: 'velocity', kind: 'velocity', size: 'wide', label: 'Threat Velocity' },
  { id: 'processed', kind: 'processed', size: 'sm', label: 'Data Processed' },
  { id: 'critical', kind: 'critical', size: 'sm', label: 'Critical Alerts' },
  { id: 'anomalies', kind: 'anomalies', size: 'sm', label: 'Anomalies' },
];

export default function Dashboard() {
  const [totalProcessed, setTotalProcessed] = useState(0);
  const [highRiskCount, setHighRiskCount] = useState(0);
  const [mediumRiskCount, setMediumRiskCount] = useState(0);
  const [chartData, setChartData] = useState<{time: string, flags: number}[]>(Array(20).fill({time: '', flags: 0}));

  // Auto-connect to the global stream (No Play/Pause button)
  useEffect(() => {
    let localHigh = 0;
    const es = new EventSource('http://localhost:8080/api/v1/simulation/stream');
    
    es.addEventListener('transaction', (e) => {
      const data = JSON.parse(e.data);
      setTotalProcessed(p => p + 1);
      
      if (data.category === 'HIGH') {
        setHighRiskCount(p => p + 1);
        localHigh++;
      }
      if (data.category === 'MEDIUM') {
        setMediumRiskCount(p => p + 1);
      }
    });

    const chartInterval = setInterval(() => {
      setChartData(prev => {
        const newData = [...prev.slice(1), { 
          time: new Date().toLocaleTimeString().split(' ')[0], 
          flags: localHigh 
        }];
        localHigh = 0;
        return newData;
      });
    }, 1000);

    return () => {
      es.close();
      clearInterval(chartInterval);
    };
  }, []);

  // --- WIDGET RENDERERS ---
  const renderWidget = (item: DashboardWidget) => {
    switch (item.kind) {
      case 'processed':
        return (
          <div className="flex flex-col h-full justify-between p-6 bg-[#0B1120] border border-cyan-500/30 rounded-2xl relative overflow-hidden group">
            <div className="absolute -right-10 -top-10 w-32 h-32 rounded-full bg-cyan-500/20 blur-3xl"></div>
            <div>
              <p className="text-cyan-400 text-xs font-bold tracking-widest uppercase mb-1">Packets Scanned</p>
              <h3 className="text-4xl font-black text-white">{totalProcessed.toLocaleString()}</h3>
            </div>
            <Activity className="w-8 h-8 text-cyan-500/50 absolute bottom-4 right-4" />
          </div>
        );
      case 'critical':
        return (
          <div className="flex flex-col h-full justify-between p-6 bg-[#0B1120] border border-red-500/30 rounded-2xl relative overflow-hidden group shadow-[0_0_15px_rgba(239,68,68,0.15)]">
            <div className="absolute -right-10 -top-10 w-32 h-32 rounded-full bg-red-500/20 blur-3xl"></div>
            <div>
              <p className="text-red-400 text-xs font-bold tracking-widest uppercase mb-1">Blocked High-Priority</p>
              <h3 className="text-4xl font-black text-white">{highRiskCount.toLocaleString()}</h3>
            </div>
            <ShieldAlert className="w-8 h-8 text-red-500/50 absolute bottom-4 right-4 animate-pulse" />
          </div>
        );
      case 'anomalies':
        return (
          <div className="flex flex-col h-full justify-between p-6 bg-[#0B1120] border border-amber-500/30 rounded-2xl relative overflow-hidden group">
            <div className="absolute -right-10 -top-10 w-32 h-32 rounded-full bg-amber-500/20 blur-3xl"></div>
            <div>
              <p className="text-amber-400 text-xs font-bold tracking-widest uppercase mb-1">Manual Review Queue</p>
              <h3 className="text-4xl font-black text-white">{mediumRiskCount.toLocaleString()}</h3>
            </div>
            <Zap className="w-8 h-8 text-amber-500/50 absolute bottom-4 right-4" />
          </div>
        );
      case 'velocity':
        return (
          <div className="h-full bg-[#0B1120] border border-slate-700/50 rounded-2xl p-6 relative overflow-hidden flex flex-col">
            <h3 className="text-sm font-bold text-white mb-2 uppercase tracking-widest flex items-center gap-2">
              <Activity className="w-4 h-4 text-red-500" /> Live Threat Velocity
            </h3>
            <div className="flex-1 min-h-[150px] -ml-4">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="colorFlags" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#ef4444" stopOpacity={0.4}/>
                      <stop offset="95%" stopColor="#ef4444" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <Tooltip contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #1e293b' }} itemStyle={{ color: '#ef4444' }} />
                  <Area type="monotone" dataKey="flags" stroke="#ef4444" strokeWidth={2} fillOpacity={1} fill="url(#colorFlags)" isAnimationActive={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        );
      case 'radar':
        return (
          <div className="h-full bg-[#0B1120] border border-cyan-500/20 rounded-2xl relative overflow-hidden flex items-center justify-center">
            {/* Animated Technical Space Radar Background */}
            <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(6,182,212,0.1)_0,transparent_70%)]"></div>
            <div className="absolute inset-0" style={{ backgroundImage: 'linear-gradient(rgba(6,182,212,0.05) 1px, transparent 1px), linear-gradient(90deg, rgba(6,182,212,0.05) 1px, transparent 1px)', backgroundSize: '20px 20px' }}></div>
            
            {/* Spinning Globe/Radar Rings */}
            <motion.div 
              animate={{ rotate: 360 }} 
              transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
              className="absolute w-64 h-64 border border-cyan-500/20 rounded-full border-dashed"
            />
            <motion.div 
              animate={{ rotate: -360 }} 
              transition={{ duration: 15, repeat: Infinity, ease: "linear" }}
              className="absolute w-48 h-48 border border-cyan-500/40 rounded-full border-dotted"
            />
            <div className="absolute w-32 h-32 border-2 border-cyan-500/60 rounded-full shadow-[0_0_30px_rgba(6,182,212,0.4)] flex items-center justify-center">
              <Globe className="w-12 h-12 text-cyan-400 animate-pulse" />
            </div>

            <div className="absolute bottom-4 left-4">
              <div className="text-cyan-400 font-mono text-xs">GLOBAL UPLINK: <span className="text-white font-bold animate-pulse">ESTABLISHED</span></div>
              <div className="text-gray-500 font-mono text-[10px]">NODE: SECTOR-7G / INTERCEPTING</div>
            </div>
          </div>
        );
      default:
        return <div>Unknown Widget</div>;
    }
  };

  return (
    <div className="relative min-h-[85vh] -m-6 p-6 overflow-hidden bg-[#050810]">
      {/* ADVANCED SPACE/TECH BACKGROUND */}
      <div className="absolute top-0 left-0 w-full h-full pointer-events-none">
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=2072&auto=format&fit=crop')] bg-cover bg-center opacity-10 mix-blend-screen"></div>
        <div className="absolute inset-0 bg-gradient-to-b from-[#050810] via-transparent to-[#050810]"></div>
        
        {/* Animated Scanning Beam */}
        <motion.div 
          animate={{ y: ['0%', '100%', '0%'] }} 
          transition={{ duration: 8, repeat: Infinity, ease: 'linear' }}
          className="w-full h-1 bg-cyan-500/20 shadow-[0_0_20px_rgba(6,182,212,0.5)]"
        />
      </div>

      <div className="relative z-10 space-y-4">
        <div className="flex justify-between items-end mb-8">
          <div>
            <h1 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-600 tracking-tighter uppercase">
              Nexus Core Overview
            </h1>
            <p className="text-cyan-500/70 font-mono text-sm tracking-widest mt-1">Autonomous Fraud Interception Protocol Active</p>
          </div>
          <div className="flex items-center gap-2 px-4 py-2 bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 rounded-full shadow-[0_0_15px_rgba(16,185,129,0.2)]">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            <span className="font-bold tracking-widest text-xs">GLOBAL STREAM ACTIVE</span>
          </div>
        </div>

        {/* DRAGGABLE GRID */}
        <DraggableWidgetGrid
          items={WIDGETS}
          renderItem={(item) => renderWidget(item as DashboardWidget)}
          className="w-full"
        />
      </div>
    </div>
  );
}
