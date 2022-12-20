/*
 * Copyright (c) 2020-2030 ZHENGGENGWEI(码匠君)<herodotus@aliyun.com>
 *
 * Dante Engine licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Dante Engine 采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改 Dante Cloud 源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://gitee.com/herodotus/dante-engine
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/herodotus/dante-engine
 * 6.若您的项目无法满足以上几点，可申请商业授权
 */

package cn.herodotus.engine.message.mailing.service;

import cn.herodotus.engine.data.core.repository.BaseRepository;
import cn.herodotus.engine.data.core.service.BaseLayeredService;
import cn.herodotus.engine.message.mailing.entity.Dialogue;
import cn.herodotus.engine.message.mailing.entity.DialogueContact;
import cn.herodotus.engine.message.mailing.entity.DialogueDetail;
import cn.herodotus.engine.message.mailing.repository.DialogueContactRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: PersonalContactService </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/12/7 22:09
 */
@Service
public class DialogueContactService extends BaseLayeredService<DialogueContact, String> {

    private static final Logger log = LoggerFactory.getLogger(DialogueContactService.class);

    private final DialogueContactRepository dialogueContactRepository;
    private final DialogueService dialogueService;
    private final DialogueDetailService dialogueDetailService;

    public DialogueContactService(DialogueContactRepository dialogueContactRepository, DialogueService dialogueService, DialogueDetailService dialogueDetailService) {
        this.dialogueContactRepository = dialogueContactRepository;
        this.dialogueService = dialogueService;
        this.dialogueDetailService = dialogueDetailService;
    }

    @Override
    public BaseRepository<DialogueContact, String> getRepository() {
        return dialogueContactRepository;
    }

    public List<DialogueContact> createContact(Dialogue dialogue, DialogueDetail dialogueDetail) {
        DialogueContact contact = new DialogueContact();
        contact.setDialogue(dialogue);
        contact.setReceiverId(dialogueDetail.getReceiverId());
        contact.setSenderId(dialogueDetail.getSenderId());
        contact.setSenderName(dialogueDetail.getSenderName());
        contact.setSenderAvatar(dialogueDetail.getSenderAvatar());

        DialogueContact reverseContext = new DialogueContact();
        reverseContext.setDialogue(dialogue);
        reverseContext.setReceiverId(dialogueDetail.getSenderId());
        reverseContext.setSenderId(dialogueDetail.getReceiverId());
        reverseContext.setSenderName(dialogueDetail.getReceiverName());
        reverseContext.setSenderAvatar(dialogueDetail.getReceiverAvatar());

        List<DialogueContact> personalContacts = new ArrayList<>();
        personalContacts.add(contact);
        personalContacts.add(reverseContext);

        log.debug("[Herodotus] |- DialogueContact Service createContact.");
        return this.saveAll(personalContacts);
    }

    public Page<DialogueContact> findByCondition(int pageNumber, int pageSize, String receiverId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<DialogueContact> specification = (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("receiverId"), receiverId));

            Predicate[] predicateArray = new Predicate[predicates.size()];
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(predicateArray)));
            return criteriaQuery.getRestriction();
        };

        log.debug("[Herodotus] |- DialogueContact Service findByCondition.");
        return this.findByPage(specification, pageable);
    }

    @Transactional
    public void deleteByDialogueId(String dialogueId) {
        dialogueDetailService.deleteAllByDialogueId(dialogueId);
        dialogueContactRepository.deleteAllByDialogueId(dialogueId);
        dialogueService.deleteById(dialogueId);
    }
}
