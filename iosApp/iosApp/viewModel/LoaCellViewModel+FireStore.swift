//
//  LoaCellViewModel+FireStore.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension LoaCellViewModel {
    
    func syncStart(force:Bool = false) {
        config.syncStart(force: force) { result in
            if (result) as! Bool {
                DispatchQueue.main.async { [weak self] in
                    self?.syncData = true
                }
                guard let uid = self.user?.uid  else {
                    print(">>>>","user is nil...")
                    self.synceEnd()
                    return
                }
                CommonRoomHelper().syncRoom(
                    userId: uid, db: self.db
                ) { error in
                    print("<>>>>>",error)
                } successAction: {
                    self.synceEnd()
                    let data = self.db.roomInfoQueriesHelper.getAllValue()
                    print(data)
                }
            } else {
                self.showSnackBar(message: "최근에 동기화를 하여 현재는 할 수 없습니다.")
            }
        } completionHandler: { error in
            print(error)
        }
    }
    
    func synceEnd() {
        showSnackBar(message: "동기화가 완료되었습니다")
        syncData = false
    }
}
