"use client";

import { useRouter } from "next/navigation";
import {
  BarChart3,
  User,
  CreditCard,
  ArrowRight,
  Banknote,
} from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Header } from "@/components/Header";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useEffect, useState } from "react";
import { authApi } from "@/lib/api";
import { toast } from "@/components/ui/use-toast";

interface UserInfo {
  id: number;
  name: string;
  email: string;
  profileImage: string;
  joinedGroups: number;
  totalBalance: number;
}

export default function ProfilePage() {
  const router = useRouter();
  const [userInfo, setUserInfo] = useState<UserInfo>({
    id: 0,
    name: "",
    email: "",
    profileImage: "/placeholder.svg?height=80&width=80",
    joinedGroups: 0,
    totalBalance: 0,
  });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await authApi.get("/api/v1/profile");
        if (response) {
          setUserInfo({
            id: 0,
            name: response.name,
            email: response.email,
            profileImage: "/placeholder.svg?height=80&width=80",
            joinedGroups: response.joinedGroups,
            totalBalance: response.accountBalance,
          });
        } else {
          console.error("Invalid API response structure:", response);
          toast({
            title: "오류",
            description: "잘못된 응답 형식입니다.",
            variant: "destructive",
          });
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error);
        toast({
          title: "오류",
          description: "사용자 정보를 불러오는데 실패했습니다.",
          variant: "destructive",
        });
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserInfo();
  }, []);

  const menuItems = [
    {
      title: "통계 현황",
      description: "나의 모임 활동 및 지출 통계",
      icon: <BarChart3 className="h-6 w-6 text-blue-500" />,
      path: "/profile/statistics",
    },
    {
      title: "마이페이지",
      description: "개인정보 및 계정 설정",
      icon: <User className="h-6 w-6 text-green-500" />,
      path: "/profile/mypage",
    },
    {
      title: "자동이체 현황",
      description: "정기 결제 및 자동이체 관리",
      icon: <CreditCard className="h-6 w-6 text-purple-500" />,
      path: "/profile/auto-transfer",
    },
    {
      title: "내 계좌",
      description: "거래내역 보기",
      icon: <Banknote className="h-6 w-6 text-yellow-500" />,
      path: "/profile/account-history",
    },
  ];

  if (isLoading || !userInfo) {
    return (
      <>
        <Header title="내 정보" />
        <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header title="내 정보" />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16">
        {/* 사용자 프로필 카드 */}
        <Card className="bg-gradient-to-r from-blue-50 to-indigo-50">
          <CardContent className="p-6">
            <div className="flex items-center">
              <Avatar className="h-20 w-20 border-4 border-white">
                <AvatarImage
                  src={
                    userInfo?.profileImage ||
                    "/placeholder.svg?height=80&width=80"
                  }
                />
                <AvatarFallback>
                  {userInfo?.name?.slice(0, 2) || "??"}
                </AvatarFallback>
              </Avatar>
              <div className="ml-4">
                <h2 className="text-xl font-bold">
                  {userInfo?.name || "사용자"}
                </h2>
                <p className="text-sm text-gray-500">
                  {userInfo?.email || "이메일 없음"}
                </p>
                <div className="mt-2 flex space-x-4">
                  <div>
                    <p className="text-xs text-gray-500">참여 모임</p>
                    <p className="font-semibold">
                      {userInfo?.joinedGroups || 0}개
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 메뉴 버튼 */}
        <div className="space-y-3">
          {menuItems.map((item, index) => (
            <Card
              key={index}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => router.push(item.path)}
            >
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    {item.icon}
                    <div className="ml-4">
                      <h3 className="font-medium">{item.title}</h3>
                      <p className="text-sm text-gray-500">
                        {item.description}
                      </p>
                    </div>
                  </div>
                  <ArrowRight className="h-5 w-5 text-gray-400" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </main>
    </>
  );
}
