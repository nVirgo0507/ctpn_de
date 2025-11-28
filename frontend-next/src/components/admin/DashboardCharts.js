import React from 'react';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
} from 'recharts';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

const DashboardCharts = ({ stats, users }) => {
    if (!stats) return null;

    const barData = [
        { name: 'Users', count: stats.totalUsers },
        { name: 'Courses', count: stats.totalCourses },
        { name: 'Consultations', count: stats.totalConsultations },
        { name: 'Blog Posts', count: stats.totalBlogPosts },
    ];

    // Process user data for Pie Charts
    const roleCounts = {};
    const statusCounts = { Active: 0, Deleted: 0 };

    if (users && users.length > 0) {
        users.forEach((user) => {
            // Role
            const role = user.userRoles && user.userRoles.length > 0 ? user.userRoles[0].role.roleName : 'USER';
            roleCounts[role] = (roleCounts[role] || 0) + 1;

            // Status
            if (user.isDeleted) {
                statusCounts.Deleted += 1;
            } else {
                statusCounts.Active += 1;
            }
        });
    }

    const roleData = Object.keys(roleCounts).map((role) => ({
        name: role,
        value: roleCounts[role],
    }));

    const statusData = [
        { name: 'Active', value: statusCounts.Active },
        { name: 'Deleted', value: statusCounts.Deleted },
    ];

    return (
        <div className="space-y-8 mt-8">
            {/* Bar Chart - System Overview */}
            <div className="bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-semibold text-gray-700 mb-4">System Overview</h2>
                <div className="h-80">
                    <ResponsiveContainer width="100%" height="100%">
                        <BarChart
                            data={barData}
                            margin={{
                                top: 5,
                                right: 30,
                                left: 20,
                                bottom: 5,
                            }}
                        >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" />
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="count" fill="#3B82F6" name="Count" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>

            {/* Pie Charts Row */}
            {users && users.length > 0 && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* User Roles Distribution */}
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h2 className="text-xl font-semibold text-gray-700 mb-4">User Roles Distribution</h2>
                        <div className="h-80">
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={roleData}
                                        cx="50%"
                                        cy="50%"
                                        labelLine={false}
                                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                        outerRadius={80}
                                        fill="#8884d8"
                                        dataKey="value"
                                    >
                                        {roleData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* User Status Distribution */}
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h2 className="text-xl font-semibold text-gray-700 mb-4">User Status Distribution</h2>
                        <div className="h-80">
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={statusData}
                                        cx="50%"
                                        cy="50%"
                                        labelLine={false}
                                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                        outerRadius={80}
                                        fill="#8884d8"
                                        dataKey="value"
                                    >
                                        {statusData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={index === 0 ? '#10B981' : '#EF4444'} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DashboardCharts;
