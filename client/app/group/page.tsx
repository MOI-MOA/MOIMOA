"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Plus, Search, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Header } from "@/components/Header"

export default function GroupsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const router = useRouter()

  // Mock data for groups
  const groups = [
    { id: 1, name: "회사 동료", description: "월간 회식 및 경비", members: 8, totalAmount: 240000, currency: "KRW" },
    { id: 2, name: "대학 친구들", description: "정기 모임", members: 6, totalAmount: 180000, currency: "KRW" },
    { id: 3, name: "가족 모임", description: "생일 및 기념일", members: 4, totalAmount: 100000, currency: "KRW" },
    { id: 4, name: "동호회", description: "주말 등산 모임", members: 12, totalAmount: 360000, currency: "KRW" },
    { id: 5, name: "독서 모임", description: "월간 독서 토론", members: 7, totalAmount: 70000, currency: "KRW" },
    { id: 6, name: "동네 친구들", description: "주말 브런치 모임", members: 5, totalAmount: 150000, currency: "KRW" },
    { id: 7, name: "스터디 그룹", description: "주 2회 영어 스터디", members: 6, totalAmount: 120000, currency: "KRW" },
  ]

  const filteredGroups = groups.filter(
    (group) =>
      group.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      group.description.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  return (
    <>
      <Header title="모임" />
      <main className="flex-1 overflow-auto p-4 space-y-4 pb-16">
        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            className="pl-9 py-2 bg-white border-gray-200"
            placeholder="모임 이름, 모임 설명, 참여인원수"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Group List Header */}
        <div className="flex justify-between items-center">
          <h2 className="text-lg font-semibold">모임</h2>
          <Button
            className="bg-blue-500 hover:bg-blue-600 text-white"
            onClick={() => router.push("/group/create-group")}
          >
            <Plus className="h-4 w-4 mr-2" />새 모임 만들기
          </Button>
        </div>

        {/* Group List */}
        <div className="space-y-3">
          {filteredGroups.map((group) => (
            <Card
              key={group.id}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => router.push(`/group/${group.id}`)}
            >
              <CardContent className="p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="font-medium text-lg text-gray-800">{group.name}</div>
                    <div className="text-sm text-gray-500">{group.description}</div>
                    <div className="text-sm mt-1 text-gray-600">
                      참여인원: {group.members}명
                      <Badge variant="secondary" className="ml-2 bg-blue-100 text-blue-800">
                        {group.totalAmount.toLocaleString()} {group.currency}
                      </Badge>
                    </div>
                  </div>
                  <ChevronRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </>
  )
}

