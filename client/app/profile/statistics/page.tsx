"use client"

import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"

export default function StatisticsPage() {
  // 기존 데이터 유지
  const monthlyExpenseData = [
    { name: "1월", amount: 120000 },
    { name: "2월", amount: 150000 },
    { name: "3월", amount: 180000 },
    { name: "4월", amount: 130000 },
    { name: "5월", amount: 160000 },
    { name: "6월", amount: 200000 },
  ]

  const groupExpenseData = [
    { name: "회사 동료", amount: 320000 },
    { name: "대학 친구들", amount: 180000 },
    { name: "가족 모임", amount: 100000 },
    { name: "동호회", amount: 250000 },
  ]

  // 모임별 참여율 데이터 추가
  const participationRateData = [
    {
      name: "회사 동료",
      attendedSchedules: 8,
      totalSchedules: 10,
      rate: 80,
    },
    {
      name: "대학 친구들",
      attendedSchedules: 5,
      totalSchedules: 8,
      rate: 62.5,
    },
    {
      name: "가족 모임",
      attendedSchedules: 4,
      totalSchedules: 4,
      rate: 100,
    },
    {
      name: "동호회",
      attendedSchedules: 6,
      totalSchedules: 12,
      rate: 50,
    },
  ]

  // 원형 차트용 데이터
  const pieChartData = participationRateData.map((item) => ({
    name: item.name,
    value: item.rate,
  }))

  // 원형 차트 색상
  const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"]

  return (
    <>
      <Header title="통계 현황" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 모임별 참여율 섹션 추가 */}
        <Card>
          <CardHeader>
            <CardTitle>모임별 참여율</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-6">
              {participationRateData.map((item, index) => (
                <div key={index} className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="font-medium text-lg">{item.name}</span>
                    <span className="text-sm text-gray-500">
                      {item.attendedSchedules}/{item.totalSchedules} 일정 참여
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-4">
                    <div
                      className="h-4 rounded-full transition-all duration-500 flex items-center justify-end pr-2"
                      style={{
                        width: `${item.rate}%`,
                        backgroundColor: COLORS[index % COLORS.length],
                      }}
                    >
                      {item.rate >= 30 && <span className="text-xs font-bold text-white">{item.rate}%</span>}
                    </div>
                  </div>
                  {item.rate < 30 && (
                    <div className="text-right text-sm font-medium" style={{ color: COLORS[index % COLORS.length] }}>
                      {item.rate}%
                    </div>
                  )}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 기존 카드들 유지 */}
        <Card>
          <CardHeader>
            <CardTitle>월별 지출 현황</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-right text-sm text-gray-500 mb-2">단위: 만원</div>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={monthlyExpenseData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis tickFormatter={(value) => (value / 10000).toFixed(0)} />
                  <Tooltip formatter={(value) => [`${(value / 10000).toFixed(1)}만원`, "금액"]} />
                  <Line
                    type="monotone"
                    dataKey="amount"
                    stroke="#3b82f6"
                    strokeWidth={2}
                    dot={{ r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>모임별 지출 현황</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-right text-sm text-gray-500 mb-2">단위: 만원</div>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={groupExpenseData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis tickFormatter={(value) => (value / 10000).toFixed(0)} />
                  <Tooltip formatter={(value) => [`${(value / 10000).toFixed(1)}만원`, "금액"]} />
                  <Line
                    type="monotone"
                    dataKey="amount"
                    stroke="#10b981"
                    strokeWidth={2}
                    dot={{ r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>통계 요약</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div className="p-4 bg-blue-50 rounded-lg">
                <p className="text-sm text-gray-500">총 지출</p>
                <p className="text-xl font-bold text-blue-600">940,000원</p>
              </div>
              <div className="p-4 bg-green-50 rounded-lg">
                <p className="text-sm text-gray-500">평균 월 지출</p>
                <p className="text-xl font-bold text-green-600">156,667원</p>
              </div>
              <div className="p-4 bg-purple-50 rounded-lg">
                <p className="text-sm text-gray-500">최대 지출 모임</p>
                <p className="text-xl font-bold text-purple-600">회사 동료</p>
              </div>
              <div className="p-4 bg-yellow-50 rounded-lg">
                <p className="text-sm text-gray-500">최대 지출 월</p>
                <p className="text-xl font-bold text-yellow-600">6월</p>
              </div>
              {/* 평균 참여율 추가 */}
              <div className="p-4 bg-indigo-50 rounded-lg">
                <p className="text-sm text-gray-500">평균 참여율</p>
                <p className="text-xl font-bold text-indigo-600">73.1%</p>
              </div>
              {/* 최고 참여율 모임 추가 */}
              <div className="p-4 bg-pink-50 rounded-lg">
                <p className="text-sm text-gray-500">최고 참여율 모임</p>
                <p className="text-xl font-bold text-pink-600">가족 모임</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </>
  )
}

