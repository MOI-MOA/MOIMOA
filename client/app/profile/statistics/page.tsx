"use client"

import { useEffect, useState } from "react"
import { authApi } from "@/lib/api"
import { Header } from "@/components/Header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"

// 기본 데이터 정의
/*
const DEFAULT_DATA = {
  monthlyExpenseData: [
    { name: "1월", amount: 120000 },
    { name: "2월", amount: 150000 },
    { name: "3월", amount: 180000 }, 
    { name: "4월", amount: 130000 },
    { name: "5월", amount: 160000 },
    { name: "6월", amount: 200000 },
  ],
  groupExpenseData: [
    { name: "회사 동료", amount: 320000 },
    { name: "대학 친구들", amount: 180000 },
    { name: "가족 모임", amount: 100000 },
    { name: "동호회", amount: 250000 },
  ],
  participationRateData: [
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
  ],
}
*/

// 원형 차트 색상
const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"]

export default function StatisticsPage() {
  const [monthlyExpenseData, setMonthlyExpenseData] = useState([])
  const [groupExpenseData, setGroupExpenseData] = useState([])
  const [participationRateData, setParticipationRateData] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await authApi.get("api/v1/profile/mypage/statistics")
        console.log("서버 응답:", response);
        
        if (response) {
          setMonthlyExpenseData(response.monthlyExpenseData)
          setGroupExpenseData(response.groupExpenseData )
          setParticipationRateData(response.participationRateData )
          console.log("api 호출 성공");
        }
      } catch (error) {
        console.error("통계 데이터를 가져오는데 실패했습니다:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  if (isLoading) {
    return (
      <>
        <Header title="통계 현황" showBackButton />
        <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
          <div className="flex items-center justify-center h-full">
            <div className="text-lg">데이터를 불러오는 중...</div>
          </div>
        </main>
      </>
    )
  }

  return (
    <>
      <Header title="통계 현황" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 모임별 참여율 섹션 */}
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

        {/* 월별 지출 현황 */}
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
                  <YAxis tickFormatter={(value: number) => (value / 10000).toFixed(0)} />
                  <Tooltip formatter={(value: number) => [`${(value / 10000).toFixed(1)}만원`, "금액"]} />
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

        {/* 모임별 지출 현황 */}
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
                  <YAxis tickFormatter={(value: number) => (value / 10000).toFixed(0)} />
                  <Tooltip formatter={(value: number) => [`${(value / 10000).toFixed(1)}만원`, "금액"]} />
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
      </main>
    </>
  )
}

