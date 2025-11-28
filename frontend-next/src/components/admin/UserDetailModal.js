import React from 'react';

const UserDetailModal = ({ user, onClose }) => {
    if (!user) return null;

    const role = user.userRoles && user.userRoles.length > 0 ? user.userRoles[0].role.roleName : 'USER';

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md mx-4 relative">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>

                <h2 className="text-2xl font-bold mb-6 text-gray-800">User Details</h2>

                <div className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-500">Full Name</label>
                        <p className="text-lg font-semibold text-gray-900">{user.fullName || 'N/A'}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-500">Email</label>
                        <p className="text-lg text-gray-900">{user.email}</p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-500">User ID</label>
                        <p className="text-sm text-gray-600 font-mono bg-gray-100 p-2 rounded">{user.userId}</p>
                    </div>

                    <div className="flex justify-between">
                        <div>
                            <label className="block text-sm font-medium text-gray-500">Role</label>
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mt-1">
                                {role}
                            </span>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-500">Status</label>
                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-1 ${user.isDeleted ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'
                                }`}>
                                {user.isDeleted ? 'Deleted' : 'Active'}
                            </span>
                        </div>
                    </div>
                </div>

                <div className="mt-8 flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition-colors"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UserDetailModal;
