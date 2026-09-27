import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import { Bell, Search } from 'lucide-react';

const Layout = () => {
  return (
    <div className="flex h-screen bg-navy-900 overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Topbar */}
        <header className="h-16 bg-navy-800 border-b border-navy-700 flex items-center justify-between px-8">
          <div className="flex items-center bg-navy-900 rounded-lg px-3 py-2 w-96 border border-navy-700">
            <Search className="w-4 h-4 text-gray-400" />
            <input 
              type="text" 
              placeholder="Search transactions, cases..." 
              className="bg-transparent border-none outline-none text-sm text-gray-200 ml-3 w-full placeholder-gray-500"
            />
          </div>
          <div className="flex items-center gap-6">
            <button className="relative text-gray-400 hover:text-white">
              <Bell className="w-5 h-5" />
              <span className="absolute -top-1 -right-1 w-2.5 h-2.5 bg-danger-500 rounded-full"></span>
            </button>
            <div className="flex items-center gap-3 border-l border-navy-700 pl-6">
              <div className="w-8 h-8 rounded-full bg-primary-500 flex items-center justify-center text-white font-medium">
                AD
              </div>
              <div className="text-sm">
                <p className="text-white font-medium leading-none">Admin User</p>
                <p className="text-gray-400 text-xs mt-1">Supervisor</p>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content Area */}
        <main className="flex-1 overflow-y-auto p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;
