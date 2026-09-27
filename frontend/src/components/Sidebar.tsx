import { NavLink } from 'react-router-dom';
import { Shield, LayoutDashboard, Activity, AlertTriangle, Settings } from 'lucide-react';

const Sidebar = () => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Transactions', path: '/transactions', icon: Activity },
    { name: 'Alerts & Cases', path: '/alerts', icon: AlertTriangle },
  ];

  return (
    <div className="w-64 bg-navy-800 border-r border-navy-700 h-screen flex flex-col">
      <div className="p-6 flex items-center gap-3">
        <Shield className="w-8 h-8 text-primary-500" />
        <span className="text-xl font-bold tracking-wider text-white">NEXUS</span>
      </div>

      <nav className="flex-1 px-4 mt-6 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.name}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                  isActive
                    ? 'bg-primary-500/10 text-primary-500'
                    : 'text-gray-400 hover:bg-navy-700 hover:text-white'
                }`
              }
            >
              <Icon className="w-5 h-5" />
              <span className="font-medium">{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="p-4 border-t border-navy-700">
        <button className="flex items-center gap-3 px-4 py-3 w-full text-gray-400 hover:bg-navy-700 hover:text-white rounded-lg transition-colors">
          <Settings className="w-5 h-5" />
          <span className="font-medium">Settings</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
