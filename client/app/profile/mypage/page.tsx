"use client";

import { useRouter } from "next/navigation";
import { Header } from "@/components/Header";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Switch } from "@/components/ui/switch";
import { Edit, User, Shield, LogOut, Bell, Key, ChevronRight } from "lucide-react";
import { useState, useEffect } from "react";
import { toast } from "@/components/ui/use-toast";
import { authApi } from "@/lib/api";
import { useAuth } from "@/app/context/AuthContext";
import { Badge } from "@/components/ui/badge";

interface UserInfo {
  name: string;
  email: string;
  birth: string;
  profileImage: string;
  notifications: {
    email: boolean;
    push: boolean;
    sms: boolean;
  };
}

export default function MyPage() {
  const router = useRouter();
  const { logout } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [userInfo, setUserInfo] = useState<UserInfo>({
    name: "",
    email: "",
    birth: "",
    profileImage: "/placeholder.svg?height=100&width=100",
    notifications: {
      email: true,
      push: true,
      sms: false,
    },
  });

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await authApi.get("/api/v1/profile/mypage");
        if (response) {
          setUserInfo({
            name: response.name,
            email: response.email,
            birth: response.birth || "",
            profileImage:
              response.profileImage || "/placeholder.svg?height=100&width=100",
            notifications: {
              email: true,
              push: true,
              sms: false,
            },
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

  const handleNotificationChange = (type: string, checked: boolean) => {
    setUserInfo((prev) => ({
      ...prev,
      notifications: {
        ...prev.notifications,
        [type]: checked,
      },
    }));

    toast({
      title: `${
        type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"
      } 알림 ${checked ? "활성화" : "비활성화"}`,
      description: `${
        type === "email" ? "이메일" : type === "push" ? "푸시" : "SMS"
      } 알림이 ${checked ? "활성화" : "비활성화"} 되었습니다.`,
    });
  };

  if (isLoading) {
    return (
      <>
        <Header title="마이페이지" showBackButton />
        <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-10 w-10 border-4 border-blue-500 border-t-transparent"></div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header title="마이페이지" showBackButton />
      <main className="flex-1 overflow-auto p-4 space-y-6 pb-16 bg-slate-50">
        {/* 프로필 정보 카드 */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <User className="h-5 w-5 mr-2 text-blue-600" />
            프로필 정보
          </h2>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-0">
              <div className="bg-gradient-to-r from-blue-500 to-indigo-500 h-24"></div>
              <div className="flex flex-col md:flex-row items-center px-6 pb-6">
                <div className="-mt-12 mb-4 md:mb-0 flex flex-col items-center md:items-start">
                  <Avatar className="h-24 w-24 border-4 border-white shadow-md">
                    <AvatarImage src={userInfo.profileImage} />
                    <AvatarFallback className="bg-blue-100 text-blue-600 text-2xl">
                      {userInfo.name.slice(0, 2)}
                    </AvatarFallback>
                  </Avatar>
                  <div className="mt-3 text-center md:text-left">
                    <h3 className="font-bold text-xl text-slate-800">{userInfo.name}</h3>
                    <p className="text-sm text-slate-500">{userInfo.email}</p>
                  </div>
                </div>
                
                <div className="mt-4 md:mt-0 md:ml-auto">
                  <Button
                    onClick={() =>
                      router.push(
                        `/profile/mypage/edit?name=${encodeURIComponent(
                          userInfo.name
                        )}`
                      )
                    }
                    className="bg-blue-600 hover:bg-blue-700 rounded-full px-4"
                  >
                    <Edit className="h-4 w-4 mr-2" />
                    프로필 수정
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 알림 설정 카드 */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <Bell className="h-5 w-5 mr-2 text-blue-600" />
            알림 설정
          </h2>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-6">
              <div className="space-y-4">
                <div className="flex items-center justify-between bg-slate-50 p-4 rounded-xl">
                  <div>
                    <div className="flex items-center">
                      <div className="bg-blue-100 p-2 rounded-full mr-3">
                        <Bell className="h-5 w-5 text-blue-600" />
                      </div>
                      <div>
                        <p className="font-medium text-slate-800">일정 알림</p>
                        <p className="text-sm text-slate-500">모임 및 일정 관련 알림</p>
                      </div>
                    </div>
                  </div>
                  <Switch
                    checked={userInfo.notifications.email}
                    onCheckedChange={(checked) =>
                      handleNotificationChange("email", checked)
                    }
                    className="data-[state=checked]:bg-blue-600"
                  />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 계정 관리 카드 */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-slate-800 flex items-center">
            <Shield className="h-5 w-5 mr-2 text-blue-600" />
            계정 관리
          </h2>
          
          <Card className="border-0 shadow-sm rounded-xl overflow-hidden">
            <CardContent className="p-0">
              <div className="divide-y divide-slate-100">
                <div className="p-4 hover:bg-slate-50 transition-colors cursor-pointer">
                  <div className="flex items-center">
                    <div className="bg-indigo-100 p-2 rounded-full mr-3">
                      <Key className="h-5 w-5 text-indigo-600" />
                    </div>
                    <div className="flex-1">
                      <p className="font-medium text-slate-800">비밀번호 변경</p>
                      <p className="text-sm text-slate-500">계정 보안을 위한 비밀번호 설정</p>
                    </div>
                    <ChevronRight className="h-5 w-5 text-slate-400" />
                  </div>
                </div>
                
                <div 
                  className="p-4 hover:bg-slate-50 transition-colors cursor-pointer"
                  onClick={() => {
                    logout();
                    router.push("/login");
                  }}
                >
                  <div className="flex items-center">
                    <div className="bg-amber-100 p-2 rounded-full mr-3">
                      <LogOut className="h-5 w-5 text-amber-600" />
                    </div>
                    <div className="flex-1">
                      <p className="font-medium text-slate-800">로그아웃</p>
                      <p className="text-sm text-slate-500">계정에서 로그아웃합니다</p>
                    </div>
                    <ChevronRight className="h-5 w-5 text-slate-400" />
                  </div>
                </div>
                
                <div className="p-4 hover:bg-red-50 transition-colors cursor-pointer">
                  <div className="flex items-center">
                    <div className="bg-red-100 p-2 rounded-full mr-3">
                      <User className="h-5 w-5 text-red-600" />
                    </div>
                    <div className="flex-1">
                      <p className="font-medium text-red-600">계정 삭제</p>
                      <p className="text-sm text-slate-500">회원 탈퇴 및 계정 정보 삭제</p>
                    </div>
                    <Badge variant="destructive" className="bg-red-100 text-red-600 border-none">위험</Badge>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </>
  );
}
