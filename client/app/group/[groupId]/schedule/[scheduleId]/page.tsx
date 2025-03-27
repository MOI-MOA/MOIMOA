"use client"
import React, { use, useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Calendar, Clock, MapPin, Users, DollarSign, MessageSquare, SendHorizontal } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"
import axios from "axios"
import { toast } from "@/components/ui/use-toast"

interface Attendee {
  userId: number
  username: string
}

interface ScheduleManager {
  userId: number
  username: string
  accountNumber?: string
}

interface ScheduleData {
  groupId: number
  groupName: string
  scheduleId: number
  scheduleTitle: string
  scheduleDetail: string
  scheduleManager: ScheduleManager
  scheduleStartTime: string
  schedulePlace: string
  attendCount: number
  totlaBudget: number
  scheduleAttendStatus: boolean
  attendees: Attendee[]
}

// 기본 데이터 설정
const defaultScheduleData: ScheduleData = {
  groupId: 101,
  groupName: "개발팀",
  scheduleId: 20240315,
  scheduleTitle: "3월 정기 회식",
  scheduleDetail: "3월 정기 회식입니다. 모든 팀원들의 참석 바랍니다.",
  scheduleManager: {
    userId: 1,
    username: "김철수",
    accountNumber: "1234567890"
  },
  scheduleStartTime: "2024-03-15T19:00:00",
  schedulePlace: "강남역 OO식당",
  attendCount: 3,
  totlaBudget: 400000,
  scheduleAttendStatus: true,
  attendees: [
    {
      userId: 1,
      username: "김철수"
    },
    {
      userId: 2,
      username: "이영희"
    },
    {
      userId: 3,
      username: "박지성"
    },
    {
      userId: 1,
      username: "김철수"
    },
    {
      userId: 2,
      username: "이영희"
    },
    {
      userId: 3,
      username: "박지성"
    },
    {
      userId: 1,
      username: "김철수"
    },
    {
      userId: 2,
      username: "이영희"
    },
    {
      userId: 3,
      username: "박지성"
    },
    {
      userId: 1,
      username: "김철수"
    },
    {
      userId: 2,
      username: "이영희"
    },
    {
      userId: 3,
      username: "박지성"
    }
  ]
}

export default function ScheduleDetailPage({ params }: { params: Promise<{ groupId: string; scheduleId: string }> }) {
  const router = useRouter()
  const { groupId, scheduleId } = use(params)
  const [scheduleData, setScheduleData] = useState<ScheduleData>(defaultScheduleData)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchScheduleData = async () => {
      try {
        const response = await axios.get(`/api/v1/schedule/${scheduleId}`)
        if (response.status === 200) {
          setScheduleData(response.data)
        }
      } catch (error) {
        console.error('일정 데이터를 불러오는데 실패했습니다:', error)
        toast({
          title: "데이터 로드 실패",
          description: "기본 데이터를 표시합니다.",
          variant: "destructive",
        })
        // 에러 발생 시 기본 데이터 사용
        setScheduleData(defaultScheduleData)
      } finally {
        setIsLoading(false)
      }
    }

    fetchScheduleData()
  }, [groupId, scheduleId])

  const handleAttendance = async () => {
    try {
      const response = await axios.post(`/api/v1/schedule/${scheduleId}/cancel`, {
        attend: !scheduleData.scheduleAttendStatus
      })
      
      if (response.status === 200) {
        setScheduleData(prev => ({
          ...prev,
          scheduleAttendStatus: !prev.scheduleAttendStatus,
          attendCount: prev.scheduleAttendStatus ? prev.attendCount - 1 : prev.attendCount + 1
        }))
        toast({
          title: scheduleData.scheduleAttendStatus ? "참석 취소 완료" : "참석 완료",
          description: scheduleData.scheduleAttendStatus ? "일정 참석이 취소되었습니다." : "일정 참석이 완료되었습니다.",
        })
      }
    } catch (error) {
      console.error('참석 상태 변경에 실패했습니다:', error)
      toast({
        title: "참석 상태 변경 실패",
        description: "잠시 후 다시 시도해주세요.",
        variant: "destructive",
      })
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
  }

  const formatTime = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
  }

  if (isLoading) {
    return <div className="flex items-center justify-center h-screen">로딩 중...</div>
  }

  return (
    <>
      <Header title="일정 상세" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        <Card>
          <CardContent className="p-6 space-y-4">
            <div>
              <h2 className="text-2xl font-bold">{scheduleData.scheduleTitle}</h2>
              <p className="text-gray-500">{scheduleData.scheduleManager.username} 주최</p>
            </div>

            <div className="space-y-2">
              <div className="flex items-center text-gray-600">
                <Calendar className="h-5 w-5 mr-2" />
                <span>{formatDate(scheduleData.scheduleStartTime)}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Clock className="h-5 w-5 mr-2" />
                <span>{formatTime(scheduleData.scheduleStartTime)}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <MapPin className="h-5 w-5 mr-2" />
                <span>{scheduleData.schedulePlace}</span>
              </div>
              <div className="flex items-center text-gray-600">
                <Users className="h-5 w-5 mr-2" />
                <span>{scheduleData.attendCount}명 참석 예정</span>
              </div>
              <div className="flex items-center text-gray-600">
                <DollarSign className="h-5 w-5 mr-2" />
                <span>예산: {scheduleData.totlaBudget.toLocaleString()}원</span>
              </div>
            </div>

            <div>
              <h3 className="font-semibold mb-2">설명</h3>
              <p className="text-gray-600">{scheduleData.scheduleDetail}</p>
            </div>

            <div>
              <h3 className="font-semibold mb-2">참석자</h3>
              <div className="flex flex-wrap gap-2">
                {scheduleData.attendees.map((attendee) => (
                  <p key={attendee.userId} className="text-gray-700">{attendee.username}</p>
                ))}
              </div>
            </div>
            <Button
              onClick={() => router.push(`/group/${groupId}/schedule/${scheduleId}/send`)}
              className="bg-gray-200 hover:bg-gray-400 text-black"
            >
              <SendHorizontal className="h-4 w-4 mr-2" />
              송금하기
            </Button>
            <div className="pt-4">
              <Button 
                className={`w-full ${
                  scheduleData.scheduleAttendStatus 
                    ? 'bg-red-500 hover:bg-red-600' 
                    : 'bg-blue-500 hover:bg-blue-600'
                } text-white`} 
                onClick={handleAttendance}
              >
                {scheduleData.scheduleAttendStatus ? "참석 취소" : "참석하기"}
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </>
  )
}

